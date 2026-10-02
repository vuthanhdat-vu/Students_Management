package com.StudentManagement.controller;

import com.StudentManagement.dto.request.StudentRequest;
import com.StudentManagement.dto.response.StudentResponse;
import com.StudentManagement.service.StudentService;
import com.StudentManagement.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponse>> create(
            @Valid @RequestBody StudentRequest request) {
        StudentResponse created = studentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Thêm sinh viên thành công", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(studentService.getAll()));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<StudentResponse>> getMyProfile() {
        return ResponseEntity.ok(ApiResponse.success(studentService.getCurrentStudent()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(studentService.getById(id)));
    }

    @GetMapping("/code/{studentCode}")
    public ResponseEntity<ApiResponse<StudentResponse>> getByCode(@PathVariable String studentCode) {
        return ResponseEntity.ok(ApiResponse.success(studentService.getByStudentCode(studentCode)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật thành công", studentService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa sinh viên thành công"));
    }
}
