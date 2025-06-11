package com.okulyonetimsistemi.okulyonetimsistemi.service;

import com.okulyonetimsistemi.okulyonetimsistemi.model.ClassEntity;
import com.okulyonetimsistemi.okulyonetimsistemi.model.Teacher;
import com.okulyonetimsistemi.okulyonetimsistemi.repository.ClassRepository;
import com.okulyonetimsistemi.okulyonetimsistemi.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassService {

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    public List<ClassEntity> getAllClasses() {
        return classRepository.findAll();
    }

    public ClassEntity saveClass(ClassEntity classEntity) {
        if (classEntity.getTeacher() != null && classEntity.getTeacher().getId() != null) {
            Teacher teacher = teacherRepository.findById(classEntity.getTeacher().getId())
                .orElseThrow(() -> new RuntimeException("Öğretmen bulunamadı!"));
            classEntity.setTeacher(teacher);
        }
        return classRepository.save(classEntity);
    }

    public ClassEntity updateClass(ClassEntity classEntity) {
        ClassEntity existingClass = classRepository.findById(classEntity.getId())
            .orElseThrow(() -> new RuntimeException("Sınıf bulunamadı!"));

        existingClass.setName(classEntity.getName());
        existingClass.setCapacity(classEntity.getCapacity());
        existingClass.setGrade(classEntity.getGrade());
        existingClass.setSection(classEntity.getSection());

        if (classEntity.getTeacher() != null && classEntity.getTeacher().getId() != null) {
            Teacher teacher = teacherRepository.findById(classEntity.getTeacher().getId())
                .orElseThrow(() -> new RuntimeException("Öğretmen bulunamadı!"));
            existingClass.setTeacher(teacher);
        } else {
            existingClass.setTeacher(null);
        }

        return classRepository.save(existingClass);
    }

    public ClassEntity getClassById(Long id) {
        return classRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sınıf bulunamadı!"));
    }

    public void deleteClass(Long id) {
        classRepository.deleteById(id);
    }

    public List<ClassEntity> findByTeacherId(Long teacherId) {
        return classRepository.findByTeacherId(teacherId);
    }
} 