package com.StudentManagement.controller;

import com.StudentManagement.service.RegistrationSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/registration")
@RequiredArgsConstructor
public class RegistrationSettingController {

    private final RegistrationSettingService service;

    @GetMapping("/status")
    public Map<String, Boolean> getStatus() {
        return Map.of("open", service.isOpen());
    }

    @PutMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Boolean> setStatus(
            @RequestBody Map<String, Boolean> request) {

        boolean open = request.getOrDefault("open", false);

        service.setOpen(open);

        return Map.of("open", open);
    }
}