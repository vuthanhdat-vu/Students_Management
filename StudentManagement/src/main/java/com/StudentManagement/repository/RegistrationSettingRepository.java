package com.StudentManagement.repository;

import com.StudentManagement.entity.RegistrationSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationSettingRepository
        extends JpaRepository<RegistrationSetting, Long> {
}