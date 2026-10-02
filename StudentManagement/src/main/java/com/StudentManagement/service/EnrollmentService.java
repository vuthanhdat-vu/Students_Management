package com.StudentManagement.service;

import com.StudentManagement.dto.request.EnrollmentRequest;
import com.StudentManagement.dto.request.ScoreUpdateRequest;
import com.StudentManagement.dto.response.EnrollmentResponse;

import java.util.List;

public interface EnrollmentService {

    EnrollmentResponse enroll(EnrollmentRequest request);

    List<EnrollmentResponse> getAll();

    List<EnrollmentResponse> getByStudentId(Long studentId);

    List<EnrollmentResponse> getMyEnrollments();

    EnrollmentResponse getById(Long id);

    EnrollmentResponse updateScore(Long id, ScoreUpdateRequest request);

    void delete(Long id);
}
