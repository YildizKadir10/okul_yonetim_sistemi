package com.okulyonetimsistemi.repository;

import com.okulyonetimsistemi.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    boolean existsByTeacherNumber(String teacherNumber);
    boolean existsByEmail(String email);
} 