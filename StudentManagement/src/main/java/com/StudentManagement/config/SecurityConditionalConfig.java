package com.StudentManagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConditionalConfig {

    @Bean
    @ConditionalOnSecurityEnabled
    public SecurityInfoService securityInfoService() {
        return new SecurityInfoService();
    }
}