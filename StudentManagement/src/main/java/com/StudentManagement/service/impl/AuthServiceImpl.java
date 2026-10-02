package com.StudentManagement.service.impl;

import com.StudentManagement.dto.request.LoginRequest;
import com.StudentManagement.dto.request.RegisterRequest;
import com.StudentManagement.dto.response.AuthResponse;
import com.StudentManagement.entity.Role;
import com.StudentManagement.entity.Student;
import com.StudentManagement.entity.User;
import com.StudentManagement.exception.ResourceNotFoundException;
import com.StudentManagement.repository.RoleRepository;
import com.StudentManagement.repository.StudentRepository;
import com.StudentManagement.repository.UserRepository;
import com.StudentManagement.service.AuthService;
import com.StudentManagement.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        String token = jwtUtils.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().getName())
                .build();
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại: " + request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank() && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã được sử dụng: " + request.getEmail());
        }

        String studentCode = request.getStudentCode();
        if (studentCode != null && !studentCode.trim().isEmpty()) {
            studentCode = studentCode.trim();
            if (studentRepository.existsByStudentCode(studentCode)) {
                throw new IllegalArgumentException("Mã sinh viên đã tồn tại: " + studentCode);
            }
        } else {
            studentCode = generateNextStudentCode();
        }

        Role studentRole = roleRepository.findById(3L)
                .orElseGet(() -> roleRepository.findByName("ROLE_STUDENT")
                        .orElseGet(() -> roleRepository.save(Role.builder().id(3L).name("ROLE_STUDENT").build())));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(studentRole)
                .build();

        user = userRepository.save(user);

        Student student = Student.builder()
                .studentCode(studentCode)
                .fullName(request.getFullName().trim())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .className(request.getClassName())
                .cohort(request.getCohort())
                .user(user)
                .build();

        studentRepository.save(student);

        String token = jwtUtils.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .build();
    }

    private String generateNextStudentCode() {
        List<String> codes = studentRepository.findAllStudentCodesStartingWithSV();
        int maxNumber = 0;
        Pattern pattern = Pattern.compile("^SV(\\d+)$");
        for (String code : codes) {
            Matcher matcher = pattern.matcher(code);
            if (matcher.matches()) {
                try {
                    int num = Integer.parseInt(matcher.group(1));
                    if (num > maxNumber) {
                        maxNumber = num;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return String.format("SV%03d", maxNumber + 1);
    }
}
