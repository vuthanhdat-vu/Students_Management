package com.StudentManagement.controller;

import com.StudentManagement.dto.request.CourseRequest;
import com.StudentManagement.dto.response.CourseResponse;
import com.StudentManagement.service.CourseService;
import com.StudentManagement.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponse>> create(
            @Valid @RequestBody CourseRequest request) {
        CourseResponse created = courseService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Thêm môn học thành công", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(courseService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật thành công", courseService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa môn học thành công"));
    }
}
