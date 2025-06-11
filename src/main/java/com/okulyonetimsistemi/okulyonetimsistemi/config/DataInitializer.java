package com.okulyonetimsistemi.okulyonetimsistemi.config;

import com.okulyonetimsistemi.okulyonetimsistemi.model.ClassEntity;
import com.okulyonetimsistemi.okulyonetimsistemi.model.Student;
import com.okulyonetimsistemi.okulyonetimsistemi.model.Teacher;
import com.okulyonetimsistemi.okulyonetimsistemi.repository.ClassRepository;
import com.okulyonetimsistemi.okulyonetimsistemi.repository.StudentRepository;
import com.okulyonetimsistemi.okulyonetimsistemi.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public void run(String... args) {
        // Öğretmenleri oluştur ve kaydet
        List<Teacher> teachers = new ArrayList<>();
        String[] subjects = {"Matematik", "Türkçe", "Fizik", "Kimya", "Biyoloji", "Tarih", "Coğrafya", "İngilizce", "Müzik", "Resim", "Din Kültürü", "Beden Eğitimi"};
        String[] teacherNames = {"Ahmet Yılmaz", "Ayşe Demir", "Mehmet Kaya", "Zeynep Arslan", "Mustafa Can", "Elif Doğan", "Burak Polat", "Cemre Yıldırım", "Deniz Tunç", "Esra Güneş", "Fatih Kurt", "Gül Şahin"};

        for (int i = 0; i < subjects.length; i++) {
            Teacher teacher = new Teacher();
            teacher.setName(teacherNames[i]);
            teacher.setSubject(subjects[i]);
            teacher.setTeacherNumber(String.format("T%03d", i + 1));
            teacher.setEmail(teacherNames[i].toLowerCase().replace(" ", ".").replace("ğ", "g").replace("ü", "u").replace("ş", "s").replace("ı", "i").replace("ö", "o").replace("ç", "c") + "@okul.com");
            teachers.add(teacherRepository.save(teacher));
        }

        // Sınıfları oluştur ve kaydet
        Random random = new Random();
        String[] sections = {"A", "B", "C", "D", "E", "F"};
        List<ClassEntity> classes = new ArrayList<>();
        int teacherIndex = 0;

        for (int grade = 9; grade <= 12; grade++) {
            for (String section : sections) {
                ClassEntity classEntity = new ClassEntity();
                classEntity.setName(grade + "-" + section);
                classEntity.setCapacity(random.nextInt(14) + 19); // 19 ile 32 arasında rastgele kapasite
                classEntity.setGrade(grade);
                classEntity.setSection(section);
                
                // Öğretmenleri sırayla sınıflara ata
                if (!teachers.isEmpty()) {
                    classEntity.setTeacher(teachers.get(teacherIndex % teachers.size()));
                    teacherIndex++;
                }
                classes.add(classRepository.save(classEntity));
            }
        }

        // Öğrencileri oluştur ve kaydet
        // İlk öğrenciyi mevcut 9-A sınıfına atayalım
        ClassEntity class9A = classRepository.findByName("9-A").orElse(null);
        if (class9A != null) {
            Student student1 = new Student();
            student1.setName("Ali Öztürk");
            student1.setStudentNumber("ogr01");
            student1.setParentPhone("05551234567");
            student1.setClassEntity(class9A);
            studentRepository.save(student1);
        }

        // İkinci öğrenciyi mevcut 10-B sınıfına atayalım
        ClassEntity class10B = classRepository.findByName("10-B").orElse(null);
        if (class10B != null) {
            Student student2 = new Student();
            student2.setName("Zeynep Yıldız");
            student2.setStudentNumber("ogr02");
            student2.setParentPhone("05559876543");
            student2.setClassEntity(class10B);
            studentRepository.save(student2);
        }
    }
} 