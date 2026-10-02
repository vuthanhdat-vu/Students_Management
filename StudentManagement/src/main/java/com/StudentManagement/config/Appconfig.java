package com.StudentManagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class Appconfig {

    @Bean
    public AdminBean adminBean() {
        return new AdminBean();
    }

    @Bean
    @Lazy
    public StudentBean studentBean() {
        return new StudentBean();
    }
}
