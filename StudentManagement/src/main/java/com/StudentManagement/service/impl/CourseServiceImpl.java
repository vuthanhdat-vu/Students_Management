package com.StudentManagement.service.impl;

import com.StudentManagement.dto.request.CourseRequest;
import com.StudentManagement.dto.response.CourseResponse;
import com.StudentManagement.entity.Course;
import com.StudentManagement.entity.Enrollment;
import com.StudentManagement.exception.ResourceNotFoundException;
import com.StudentManagement.repository.CourseRepository;
import com.StudentManagement.repository.EnrollmentRepository;
import com.StudentManagement.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public CourseResponse create(CourseRequest request) {
        validateWeights(request.getProcessWeight(), request.getComponentWeight());

        Course course = Course.builder()
                .courseName(request.getCourseName())
                .numberOfSessions(request.getNumberOfSessions())
                .processWeight(request.getProcessWeight())
                .componentWeight(request.getComponentWeight())
                .build();

        return toResponse(courseRepository.save(course));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getAll() {
        return courseRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Môn học", "id", id));
        return toResponse(course);
    }

    @Override
    public CourseResponse update(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Môn học", "id", id));

        validateWeights(request.getProcessWeight(), request.getComponentWeight());

        course.setCourseName(request.getCourseName());
        course.setNumberOfSessions(request.getNumberOfSessions());
        course.setProcessWeight(request.getProcessWeight());
        course.setComponentWeight(request.getComponentWeight());

        return toResponse(courseRepository.save(course));
    }

    @Override
    public void delete(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Môn học", "id", id));

        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(id);
        if (!enrollments.isEmpty()) {
            enrollmentRepository.deleteAll(enrollments);
        }

        courseRepository.delete(course);
    }

    private void validateWeights(Double processWeight, Double componentWeight) {
        double total = processWeight + componentWeight;
        if (Math.abs(total - 1.0) > 0.001) {
            throw new IllegalArgumentException(
                    String.format("Tổng tỷ lệ điểm phải bằng 1.0, hiện tại: %.2f", total));
        }
    }

    private CourseResponse toResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .courseName(course.getCourseName())
                .numberOfSessions(course.getNumberOfSessions())
                .processWeight(course.getProcessWeight())
                .componentWeight(course.getComponentWeight())
                .build();
    }
}
