package com.StudentManagement.entity;

import com.StudentManagement.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String studentCode;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate dateOfBirth;

    @Column(length = 50)
    private String className;

    @Column(length = 20)
    private String cohort;

    @OneToOne(fetch = FetchType.EAGER)
    //Class Student
    @JoinColumn(name = "user_id", unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;
    //Class Student
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Enrollment> enrollments = new ArrayList<>();
}
