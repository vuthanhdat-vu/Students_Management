package com.StudentManagement.service.impl;

import com.StudentManagement.dto.request.StudentRequest;
import com.StudentManagement.dto.response.StudentResponse;
import com.StudentManagement.entity.Enrollment;
import com.StudentManagement.entity.Role;
import com.StudentManagement.entity.Student;
import com.StudentManagement.entity.User;
import com.StudentManagement.exception.ResourceNotFoundException;
import com.StudentManagement.repository.EnrollmentRepository;
import com.StudentManagement.repository.RoleRepository;
import com.StudentManagement.repository.StudentRepository;
import com.StudentManagement.repository.UserRepository;
import com.StudentManagement.service.StudentService;
import com.StudentManagement.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public StudentResponse create(StudentRequest request) {
        String studentCode = request.getStudentCode();
        if (studentCode != null && !studentCode.trim().isEmpty()) {
            studentCode = studentCode.trim();
            if (studentRepository.existsByStudentCode(studentCode)) {
                throw new IllegalArgumentException("Mã sinh viên đã tồn tại: " + studentCode);
            }
        } else {
            studentCode = generateNextStudentCode();
        }

        // Tạo tài khoản User nếu có username và password
        User linkedUser = null;
        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()
                && request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            if (userRepository.existsByUsername(request.getUsername().trim())) {
                throw new IllegalArgumentException("Tên đăng nhập đã tồn tại: " + request.getUsername());
            }
            if (request.getEmail() != null && !request.getEmail().isBlank()
                    && userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email đã được sử dụng: " + request.getEmail());
            }
            Role studentRole = roleRepository.findById(3L)
                    .orElseGet(() -> roleRepository.findByName("ROLE_STUDENT")
                            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy role ROLE_STUDENT")));
            linkedUser = User.builder()
                    .username(request.getUsername().trim())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .email(request.getEmail())
                    .role(studentRole)
                    .build();
            linkedUser = userRepository.save(linkedUser);
        }

        Student student = Student.builder()
                .studentCode(studentCode)
                .fullName(request.getFullName().trim())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .className(request.getClassName())
                .cohort(request.getCohort())
                .user(linkedUser)
                .build();

        return toResponse(studentRepository.save(student));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getAll() {
        return studentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên", "id", id));
        validateStudentAccess(student);
        return toResponse(student);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getByStudentCode(String studentCode) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên", "mã SV", studentCode));
        validateStudentAccess(student);
        return toResponse(student);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getCurrentStudent() {
        String username = SecurityUtils.getCurrentUsername();

        Student student = studentRepository.findByUserUsername(username)
                .or(() -> studentRepository.findByStudentCode(username))
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin sinh viên của tài khoản: " + username));

        return toResponse(student);
    }

    @Override
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên", "id", id));

        if (request.getStudentCode() != null && !request.getStudentCode().trim().isEmpty()) {
            String newCode = request.getStudentCode().trim();
            if (!newCode.equalsIgnoreCase(student.getStudentCode()) && studentRepository.existsByStudentCode(newCode)) {
                throw new IllegalArgumentException("Mã sinh viên đã tồn tại: " + newCode);
            }
            student.setStudentCode(newCode);
        }

        student.setFullName(request.getFullName().trim());
        student.setGender(request.getGender());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setClassName(request.getClassName());
        student.setCohort(request.getCohort());
        return toResponse(studentRepository.save(student));
    }

    @Override
    public void delete(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên", "id", id));
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(id);
        if (!enrollments.isEmpty()) {
            enrollmentRepository.deleteAll(enrollments);
        }
        User user = student.getUser();
        student.setUser(null);
        studentRepository.delete(student);

        if (user != null) {
            userRepository.delete(user);
        }
    }

    private void validateStudentAccess(Student student) {
        if (SecurityUtils.isStudent() && !SecurityUtils.isAdminOrTeacher()) {
            String currentUsername = SecurityUtils.getCurrentUsername();
            boolean isOwner = (student.getUser() != null && currentUsername.equals(student.getUser().getUsername()))
                    || currentUsername.equals(student.getStudentCode());
            if (!isOwner) {
                throw new AccessDeniedException("Bạn không có quyền xem thông tin của sinh viên này");
            }
        }
    }

    private String generateNextStudentCode() {
        List<String> codes = studentRepository.findAllStudentCodesStartingWithSV();
        int maxNumber = 0;
        Pattern pattern = Pattern.compile("^SV(\\d+)$");
        for (String code : codes) {
            Matcher matcher = pattern.matcher(code);
            if (matcher.matches()) {
                try {
                    int num = Integer.parseInt(matcher.group(1));
                    if (num > maxNumber) {
                        maxNumber = num;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return String.format("SV%03d", maxNumber + 1);
    }

    private StudentResponse toResponse(Student student) {
        long count = enrollmentRepository.countByStudentId(student.getId());
        return StudentResponse.builder()
                .id(student.getId())
                .studentCode(student.getStudentCode())
                .fullName(student.getFullName())
                .gender(student.getGender())
                .dateOfBirth(student.getDateOfBirth())
                .className(student.getClassName())
                .cohort(student.getCohort())
                .enrollmentCount(count)
                .build();
    }
}
