package com.StudentManagement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "enrollments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //Class Enrollment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private Double processScore;
    private Double componentScore;

    @Transient
    public Double getFinalScore() {
        if (processScore == null || componentScore == null || course == null) {
            return null;
        }
        return processScore * course.getProcessWeight()
                + componentScore * course.getComponentWeight();
    }

    @Transient
    public Boolean isPassed() {
        Double finalScore = getFinalScore();
        return finalScore != null && finalScore >= 4.0;
    }
}
