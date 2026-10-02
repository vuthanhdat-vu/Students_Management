package com.StudentManagement.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseRequest {

    @NotBlank(message = "Tên môn học không được để trống")
    private String courseName;

    private Integer numberOfSessions;

    @NotNull(message = "Tỷ lệ điểm quá trình không được để trống")
    @DecimalMin(value = "0.0", message = "Tỷ lệ phải từ 0.0 đến 1.0")
    @DecimalMax(value = "1.0", message = "Tỷ lệ phải từ 0.0 đến 1.0")
    private Double processWeight;

    @NotNull(message = "Tỷ lệ điểm thành phần không được để trống")
    @DecimalMin(value = "0.0", message = "Tỷ lệ phải từ 0.0 đến 1.0")
    @DecimalMax(value = "1.0", message = "Tỷ lệ phải từ 0.0 đến 1.0")
    private Double componentWeight;
}
