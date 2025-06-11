package com.okulyonetimsistemi.okulyonetimsistemi.repository;

import com.okulyonetimsistemi.okulyonetimsistemi.model.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {
    List<ClassEntity> findByTeacherId(Long teacherId);
    Optional<ClassEntity> findByName(String name);
} 