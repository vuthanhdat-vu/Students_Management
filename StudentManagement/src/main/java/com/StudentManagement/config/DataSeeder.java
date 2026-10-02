package com.StudentManagement.config;

import com.StudentManagement.entity.Role;
import com.StudentManagement.entity.User;
import com.StudentManagement.repository.RoleRepository;
import com.StudentManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        Role adminRole = roleRepository.findById(1L).orElseGet(() ->
                roleRepository.save(Role.builder().id(1L).name("ROLE_ADMIN").build()));
        Role teachRole = roleRepository.findById(2L).orElseGet(() ->
                roleRepository.save(Role.builder().id(2L).name("ROLE_TEACH").build()));
        Role studentRole = roleRepository.findById(3L).orElseGet(() ->
                roleRepository.save(Role.builder().id(3L).name("ROLE_STUDENT").build()));
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("123456"))
                    .email("admin@gmail.com")
                    .role(adminRole)
                    .build();

            userRepository.save(admin);
        }
    }
}
