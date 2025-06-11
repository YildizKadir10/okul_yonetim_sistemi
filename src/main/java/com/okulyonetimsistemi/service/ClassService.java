package com.okulyonetimsistemi.service;

import com.okulyonetimsistemi.model.Class;
import com.okulyonetimsistemi.repository.ClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClassService {

    @Autowired
    private ClassRepository classRepository;

    public List<Class> getAllClasses() {
        return classRepository.findAll();
    }

    public Optional<Class> getClassById(Long id) {
        return classRepository.findById(id);
    }

    public Class createClass(Class classEntity) {
        if (classRepository.existsByName(classEntity.getName())) {
            throw new RuntimeException("Bu sınıf adı zaten kullanılıyor.");
        }
        return classRepository.save(classEntity);
    }

    public Class updateClass(Long id, Class classDetails) {
        Class classEntity = classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sınıf bulunamadı."));

        classEntity.setName(classDetails.getName());
        classEntity.setGrade(classDetails.getGrade());
        classEntity.setSection(classDetails.getSection());
        classEntity.setTeacher(classDetails.getTeacher());
        classEntity.setStudents(classDetails.getStudents());

        return classRepository.save(classEntity);
    }

    public void deleteClass(Long id) {
        Class classEntity = classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sınıf bulunamadı."));
        classRepository.delete(classEntity);
    }
} 