package com.StudentManagement.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollmentRequest {

    private Long studentId;

    @NotNull(message = "ID môn học không được để trống")
    private Long courseId;
}
