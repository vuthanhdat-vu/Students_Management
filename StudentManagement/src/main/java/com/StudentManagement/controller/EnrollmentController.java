package com.StudentManagement.controller;

import com.StudentManagement.dto.request.EnrollmentRequest;
import com.StudentManagement.dto.request.ScoreUpdateRequest;
import com.StudentManagement.dto.response.EnrollmentResponse;
import com.StudentManagement.service.EnrollmentService;
import com.StudentManagement.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enroll(
            @Valid @RequestBody EnrollmentRequest request) {
        EnrollmentResponse response = enrollmentService.enroll(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đăng ký môn học thành công", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success(enrollmentService.getAll())
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getMyEnrollments() {
        return ResponseEntity.ok(ApiResponse.success(enrollmentService.getMyEnrollments()));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getByStudent(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success(enrollmentService.getByStudentId(studentId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(enrollmentService.getById(id)));
    }

    @PutMapping("/{id}/score")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> updateScore(
            @PathVariable Long id,
            @Valid @RequestBody ScoreUpdateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật điểm thành công", enrollmentService.updateScore(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        enrollmentService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Hủy đăng ký thành công"));
    }
}
