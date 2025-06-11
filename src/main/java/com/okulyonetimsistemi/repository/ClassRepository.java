package com.okulyonetimsistemi.repository;

import com.okulyonetimsistemi.model.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {
    boolean existsByName(String name);
} 