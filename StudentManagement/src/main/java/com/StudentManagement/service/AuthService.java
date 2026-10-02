package com.StudentManagement.service;

import com.StudentManagement.dto.request.LoginRequest;
import com.StudentManagement.dto.request.RegisterRequest;
import com.StudentManagement.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request);
}
