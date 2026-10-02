package com.StudentManagement.service;

import com.StudentManagement.dto.request.StudentRequest;
import com.StudentManagement.dto.response.StudentResponse;

import java.util.List;

public interface StudentService {

    List<StudentResponse> getAll();

    StudentResponse getById(Long id);

    StudentResponse getByStudentCode(String studentCode);

    StudentResponse getCurrentStudent();

    StudentResponse create(StudentRequest request);

    StudentResponse update(Long id, StudentRequest request);

    void delete(Long id);
}
