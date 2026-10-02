package com.StudentManagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {

    private Long id;

    private Long studentId;
    private String studentCode;
    private String studentName;

    private Long courseId;
    private String courseName;
    private Integer numberOfSessions;

    private Double processScore;
    private Double componentScore;
    private Double finalScore;
    private Boolean passed;
    private String status;
}
