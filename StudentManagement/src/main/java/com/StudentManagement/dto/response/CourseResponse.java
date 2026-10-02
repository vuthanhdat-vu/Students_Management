package com.StudentManagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {

    private Long id;
    private String courseName;
    private Integer numberOfSessions;
    private Double processWeight;
    private Double componentWeight;
}
