package com.StudentManagement.service.impl;

import com.StudentManagement.dto.request.TeacherCreateRequest;
import com.StudentManagement.dto.response.TeacherResponse;
import com.StudentManagement.entity.Role;
import com.StudentManagement.entity.User;
import com.StudentManagement.exception.ResourceNotFoundException;
import com.StudentManagement.repository.RoleRepository;
import com.StudentManagement.repository.UserRepository;
import com.StudentManagement.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherServiceImpl implements TeacherService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public TeacherResponse createTeacher(TeacherCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại: " + request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank() && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã được sử dụng: " + request.getEmail());
        }

        Role teacherRole = roleRepository.findById(2L)
                .orElseGet(() -> roleRepository.findByName("ROLE_TEACH")
                        .orElseGet(() -> roleRepository.save(Role.builder().id(2L).name("ROLE_TEACH").build())));

        User teacher = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(teacherRole)
                .build();

        return toResponse(userRepository.save(teacher));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherResponse> getAllTeachers() {
        return userRepository.findByRoleName("ROLE_TEACH")
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherResponse getTeacherById(Long id) {
        User user = userRepository.findById(id)
                .filter(u -> u.getRole() != null && "ROLE_TEACH".equals(u.getRole().getName()))
                .orElseThrow(() -> new ResourceNotFoundException("Giáo viên", "id", id));
        return toResponse(user);
    }

    @Override
    public void deleteTeacher(Long id) {
        User user = userRepository.findById(id)
                .filter(u -> u.getRole() != null && "ROLE_TEACH".equals(u.getRole().getName()))
                .orElseThrow(() -> new ResourceNotFoundException("Giáo viên", "id", id));
        userRepository.delete(user);
    }

    private TeacherResponse toResponse(User user) {
        return TeacherResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().getName() : null)
                .build();
    }
}
