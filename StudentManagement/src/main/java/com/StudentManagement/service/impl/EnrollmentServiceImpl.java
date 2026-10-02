package com.StudentManagement.service.impl;

import com.StudentManagement.dto.request.EnrollmentRequest;
import com.StudentManagement.dto.request.ScoreUpdateRequest;
import com.StudentManagement.dto.response.EnrollmentResponse;
import com.StudentManagement.entity.Course;
import com.StudentManagement.entity.Enrollment;
import com.StudentManagement.entity.Student;
import com.StudentManagement.exception.ResourceNotFoundException;
import com.StudentManagement.repository.CourseRepository;
import com.StudentManagement.repository.EnrollmentRepository;
import com.StudentManagement.repository.StudentRepository;
import com.StudentManagement.service.EnrollmentService;
import com.StudentManagement.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.StudentManagement.service.RegistrationSettingService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final RegistrationSettingService registrationSettingService;

    @Override
    public EnrollmentResponse enroll(EnrollmentRequest request) {
        if (SecurityUtils.isStudent() && !registrationSettingService.isOpen()) {
            throw new IllegalStateException("Cổng đăng ký học phần hiện đang đóng.");
        }

        Long targetStudentId = resolveAndValidateStudentIdForEnroll(request.getStudentId());

        Student student = studentRepository.findById(targetStudentId)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên", "id", targetStudentId));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Môn học", "id", request.getCourseId()));

        if (enrollmentRepository.existsByStudentIdAndCourseId(targetStudentId, request.getCourseId())) {
            throw new IllegalArgumentException(
                    String.format("Sinh viên '%s' đã đăng ký môn '%s'",
                            student.getStudentCode(), course.getCourseName()));
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .build();

        return toResponse(enrollmentRepository.save(enrollment));
    }

    @Override
    public List<EnrollmentResponse> getAll() {
        return enrollmentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getByStudentId(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Sinh viên", "id", studentId);
        }

        validateStudentAccess(studentId);

        return enrollmentRepository.findByStudentId(studentId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getMyEnrollments() {
        Student currentStudent = getCurrentStudent();
        return enrollmentRepository.findByStudentId(currentStudent.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentResponse getById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đăng ký", "id", id));

        validateStudentAccess(enrollment.getStudent().getId());

        return toResponse(enrollment);
    }

    @Override
    public EnrollmentResponse updateScore(Long id, ScoreUpdateRequest request) {
        if (SecurityUtils.isStudent() && !SecurityUtils.isAdminOrTeacher()) {
            throw new AccessDeniedException("Chỉ quản trị viên hoặc giáo viên mới có quyền nhập điểm");
        }

        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đăng ký", "id", id));

        if (request.getProcessScore() != null) {
            enrollment.setProcessScore(request.getProcessScore());
        }
        if (request.getComponentScore() != null) {
            enrollment.setComponentScore(request.getComponentScore());
        }

        return toResponse(enrollmentRepository.save(enrollment));
    }

    @Override
    public void delete(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đăng ký", "id", id));
        if (SecurityUtils.isStudent() && !registrationSettingService.isOpen()) {
            throw new IllegalStateException("Cổng đăng ký học phần hiện đang đóng.");
        }

        if (SecurityUtils.isStudent() && !SecurityUtils.isAdminOrTeacher()) {
            Student currentStudent = getCurrentStudent();
            if (!enrollment.getStudent().getId().equals(currentStudent.getId())) {
                throw new AccessDeniedException("Học sinh chỉ có thể hủy đăng ký môn học của chính mình");
            }
        }

        enrollmentRepository.delete(enrollment);
    }

    private Long resolveAndValidateStudentIdForEnroll(Long requestedStudentId) {
        if (SecurityUtils.isStudent() && !SecurityUtils.isAdminOrTeacher()) {
            Student currentStudent = getCurrentStudent();
            if (requestedStudentId != null && !requestedStudentId.equals(currentStudent.getId())) {
                throw new AccessDeniedException("Học sinh chỉ có thể tự đăng ký môn học cho chính mình");
            }
            return currentStudent.getId();
        }

        if (requestedStudentId == null) {
            throw new IllegalArgumentException("Vui lòng cung cấp ID sinh viên (studentId)");
        }
        return requestedStudentId;
    }

    private void validateStudentAccess(Long studentId) {
        if (SecurityUtils.isStudent() && !SecurityUtils.isAdminOrTeacher()) {
            Student currentStudent = getCurrentStudent();
            if (!studentId.equals(currentStudent.getId())) {
                throw new AccessDeniedException("Bạn chỉ có quyền xem điểm và môn học của chính mình");
            }
        }
    }

    private Student getCurrentStudent() {
        String username = SecurityUtils.getCurrentUsername();

        return studentRepository.findByUserUsername(username)
                .or(() -> studentRepository.findByStudentCode(username))
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin sinh viên của tài khoản: " + username));
    }

    private EnrollmentResponse toResponse(Enrollment e) {
        Double finalScore = null;
        Boolean passed = null;
        String status = "CHƯA CÓ ĐIỂM";

        if (e.getProcessScore() != null && e.getComponentScore() != null && e.getCourse() != null) {
            double rawFinal = e.getProcessScore() * e.getCourse().getProcessWeight()
                    + e.getComponentScore() * e.getCourse().getComponentWeight();
            finalScore = Math.round(rawFinal * 100.0) / 100.0;
            passed = finalScore >= 4.0;
            status = passed ? "ĐỖ" : "TRƯỢT";
        }

        return EnrollmentResponse.builder()
                .id(e.getId())
                .studentId(e.getStudent().getId())
                .studentCode(e.getStudent().getStudentCode())
                .studentName(e.getStudent().getFullName())
                .courseId(e.getCourse().getId())
                .courseName(e.getCourse().getCourseName())
                .numberOfSessions(e.getCourse().getNumberOfSessions())
                .processScore(e.getProcessScore())
                .componentScore(e.getComponentScore())
                .finalScore(finalScore)
                .passed(passed)
                .status(status)
                .build();
    }
}
