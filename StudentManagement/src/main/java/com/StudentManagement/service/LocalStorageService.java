package com.StudentManagement.service;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class LocalStorageService {

    public void save() {
        System.out.println("Lưu file vào máy local");
    }
}