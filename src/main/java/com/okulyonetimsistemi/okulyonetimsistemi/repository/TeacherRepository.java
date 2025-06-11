package com.okulyonetimsistemi.okulyonetimsistemi.repository;

import com.okulyonetimsistemi.okulyonetimsistemi.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    List<Teacher> findBySubject(String subject);
    boolean existsByEmail(String email);
    Optional<Teacher> findTopByOrderByTeacherNumberDesc();
    boolean existsByTeacherNumber(String teacherNumber);
} 