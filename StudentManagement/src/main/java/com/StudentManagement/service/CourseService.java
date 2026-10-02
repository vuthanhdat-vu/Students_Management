package com.StudentManagement.service;

import com.StudentManagement.dto.request.CourseRequest;
import com.StudentManagement.dto.response.CourseResponse;

import java.util.List;

public interface CourseService {

    List<CourseResponse> getAll();

    CourseResponse getById(Long id);

    CourseResponse create(CourseRequest request);

    CourseResponse update(Long id, CourseRequest request);

    void delete(Long id);
}
