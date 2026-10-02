package com.StudentManagement.dto.response;

import com.StudentManagement.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {

    private Long id;
    private String studentCode;
    private String fullName;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String className;
    private String cohort;
    private long enrollmentCount;
}
