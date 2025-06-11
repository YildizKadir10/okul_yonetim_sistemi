package com.okulyonetimsistemi.okulyonetimsistemi.controller;

import com.okulyonetimsistemi.okulyonetimsistemi.model.ClassEntity;
import com.okulyonetimsistemi.okulyonetimsistemi.model.Student;
import com.okulyonetimsistemi.okulyonetimsistemi.service.ClassService;
import com.okulyonetimsistemi.okulyonetimsistemi.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private StudentService studentService;

    @Autowired
    private ClassService classService;

    @GetMapping
    public String listStudents(Model model) {
        List<Student> students = studentService.getAllStudents();
        List<ClassEntity> classes = classService.getAllClasses();
        model.addAttribute("students", students);
        model.addAttribute("classes", classes);
        return "students";
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<?> createStudent(@RequestBody Student student) {
        try {
            logger.info("Yeni öğrenci ekleniyor: {}", student.getName());
            Student savedStudent = studentService.saveStudent(student);
            logger.info("Öğrenci başarıyla eklendi: {}", savedStudent.getName());
            return ResponseEntity.ok(savedStudent);
        } catch (RuntimeException e) {
            logger.warn("Öğrenci eklenirken validasyon hatası veya iş mantığı hatası: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Öğrenci eklenirken beklenmeyen bir hata oluştu", e);
            return ResponseEntity.internalServerError().body("Öğrenci eklenirken beklenmeyen bir hata oluştu: " + e.getMessage());
        }
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        try {
            logger.info("Öğrenci bilgileri alınıyor. ID: {}", id);
            Student student = studentService.getStudentById(id)
                    .orElseThrow(() -> new RuntimeException("Öğrenci bulunamadı!"));
            logger.info("Öğrenci bulundu: {}", student.getName());
            return ResponseEntity.ok(student);
        } catch (RuntimeException e) {
            logger.warn("Öğrenci bulunamadı. ID: {}, Hata: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Öğrenci bilgileri alınırken beklenmeyen hata oluştu. ID: {}", id, e);
            return ResponseEntity.internalServerError().body("Öğrenci bilgileri alınırken bir hata oluştu: " + e.getMessage());
        }
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        try {
            student.setId(id);
            Student updatedStudent = studentService.updateStudent(student);
            return ResponseEntity.ok(updatedStudent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        try {
            studentService.deleteStudent(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
} 