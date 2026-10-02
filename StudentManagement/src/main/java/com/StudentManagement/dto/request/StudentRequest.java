package com.StudentManagement.dto.request;

import com.StudentManagement.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentRequest {

    private String studentCode;

    @NotBlank(message = "Tên sinh viên không được để trống")
    private String fullName;

    @NotNull(message = "Giới tính không được để trống")
    private Gender gender;

    private LocalDate dateOfBirth;

    private String className;

    private String cohort;

    // Optional: nếu cung cấp, sẽ tạo tài khoản User liên kết cho sinh viên
    private String username;
    private String password;
    private String email;
}
