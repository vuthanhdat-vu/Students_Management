package com.StudentManagement.service.impl;

import com.StudentManagement.entity.RegistrationSetting;
import com.StudentManagement.repository.RegistrationSettingRepository;
import com.StudentManagement.service.RegistrationSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RegistrationSettingServiceImpl
        implements RegistrationSettingService {

    private final RegistrationSettingRepository repository;

    @Override
    @Transactional(readOnly = true)
    public boolean isOpen() {
        return repository.findById(1L)
                .map(RegistrationSetting::isOpen)
                .orElse(false);
    }

    @Override
    public void setOpen(boolean open) {
        RegistrationSetting setting = repository.findById(1L)
                .orElseGet(() -> {
                    RegistrationSetting newSetting = new RegistrationSetting();
                    newSetting.setId(1L);
                    return newSetting;
                });

        setting.setOpen(open);
        repository.save(setting);
    }
}