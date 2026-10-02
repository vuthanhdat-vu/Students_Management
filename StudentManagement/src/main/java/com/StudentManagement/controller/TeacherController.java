package com.StudentManagement.controller;

import com.StudentManagement.dto.request.TeacherCreateRequest;
import com.StudentManagement.dto.response.TeacherResponse;
import com.StudentManagement.service.TeacherService;
import com.StudentManagement.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping
    public ResponseEntity<ApiResponse<TeacherResponse>> create(
            @Valid @RequestBody TeacherCreateRequest request) {
        TeacherResponse created = teacherService.createTeacher(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo tài khoản giáo viên thành công", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TeacherResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(teacherService.getAllTeachers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TeacherResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(teacherService.getTeacherById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa tài khoản giáo viên thành công"));
    }
}
