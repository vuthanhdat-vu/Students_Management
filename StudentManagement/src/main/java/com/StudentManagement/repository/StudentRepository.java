package com.StudentManagement.repository;

import com.StudentManagement.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByStudentCode(String studentCode);

    boolean existsByStudentCode(String studentCode);

    Optional<Student> findByUserUsername(String username);

    @Query("SELECT s.studentCode FROM Student s WHERE s.studentCode LIKE 'SV%'")
    List<String> findAllStudentCodesStartingWithSV();
}
