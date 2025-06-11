package com.okulyonetimsistemi.service;

import com.okulyonetimsistemi.model.Teacher;
import com.okulyonetimsistemi.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public Optional<Teacher> getTeacherById(Long id) {
        return teacherRepository.findById(id);
    }

    public Teacher createTeacher(Teacher teacher) {
        if (teacherRepository.existsByTeacherNumber(teacher.getTeacherNumber())) {
            throw new RuntimeException("Bu öğretmen numarası zaten kullanılıyor.");
        }
        if (teacherRepository.existsByEmail(teacher.getEmail())) {
            throw new RuntimeException("Bu e-posta adresi zaten kullanılıyor.");
        }
        return teacherRepository.save(teacher);
    }

    public Teacher updateTeacher(Long id, Teacher teacherDetails) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Öğretmen bulunamadı."));

        teacher.setFirstName(teacherDetails.getFirstName());
        teacher.setLastName(teacherDetails.getLastName());
        teacher.setEmail(teacherDetails.getEmail());
        teacher.setPhoneNumber(teacherDetails.getPhoneNumber());
        teacher.setAddress(teacherDetails.getAddress());
        teacher.setBirthDate(teacherDetails.getBirthDate());
        teacher.setSubject(teacherDetails.getSubject());

        return teacherRepository.save(teacher);
    }

    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Öğretmen bulunamadı."));
        teacherRepository.delete(teacher);
    }
} 