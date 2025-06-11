package com.okulyonetimsistemi.okulyonetimsistemi.repository;

import com.okulyonetimsistemi.okulyonetimsistemi.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByName(String name);
    Optional<Student> findTopByOrderByStudentNumberDesc();
    boolean existsByStudentNumber(String studentNumber);
} 