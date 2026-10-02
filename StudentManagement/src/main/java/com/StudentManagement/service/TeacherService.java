package com.StudentManagement.service;

import com.StudentManagement.dto.request.TeacherCreateRequest;
import com.StudentManagement.dto.response.TeacherResponse;

import java.util.List;

public interface TeacherService {

    TeacherResponse createTeacher(TeacherCreateRequest request);

    List<TeacherResponse> getAllTeachers();

    TeacherResponse getTeacherById(Long id);

    void deleteTeacher(Long id);
}
