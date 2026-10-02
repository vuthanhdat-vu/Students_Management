package com.StudentManagement.repository;

import com.StudentManagement.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentId(Long studentId);

    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    List<Enrollment> findByCourseId(Long courseId);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.student.id = :studentId")
    long countByStudentId(@Param("studentId") Long studentId);
}
