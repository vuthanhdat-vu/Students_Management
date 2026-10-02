package com.StudentManagement.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

@Data
public class ScoreUpdateRequest {

    @DecimalMin(value = "0.0", message = "Điểm phải từ 0 đến 10")
    @DecimalMax(value = "10.0", message = "Điểm phải từ 0 đến 10")
    private Double processScore;

    @DecimalMin(value = "0.0", message = "Điểm phải từ 0 đến 10")
    @DecimalMax(value = "10.0", message = "Điểm phải từ 0 đến 10")
    private Double componentScore;
}
