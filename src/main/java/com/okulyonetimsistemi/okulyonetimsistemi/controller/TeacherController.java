package com.okulyonetimsistemi.okulyonetimsistemi.controller;

import com.okulyonetimsistemi.okulyonetimsistemi.dto.TeacherDto;
import com.okulyonetimsistemi.okulyonetimsistemi.model.Teacher;
import com.okulyonetimsistemi.okulyonetimsistemi.service.TeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/teachers")
public class TeacherController {
    private static final Logger logger = LoggerFactory.getLogger(TeacherController.class);

    @Autowired
    private TeacherService teacherService;

    @GetMapping
    public String listTeachers(Model model) {
        model.addAttribute("teachers", teacherService.getAllTeacherDtos());
        return "teachers";
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<TeacherDto>> getAllTeachersApi() {
        try {
            return ResponseEntity.ok(teacherService.getAllTeacherDtos());
        } catch (Exception e) {
            logger.error("Tüm öğretmenler alınırken hata oluştu", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> getTeacherById(@PathVariable Long id) {
        try {
            logger.info("Öğretmen bilgileri alınıyor. ID: {}", id);
            TeacherDto teacherDto = teacherService.getTeacherById(id)
                .orElseThrow(() -> new RuntimeException("Öğretmen bulunamadı!"));
            logger.info("Öğretmen bulundu: {}", teacherDto.getName());
            return ResponseEntity.ok(teacherDto);
        } catch (RuntimeException e) {
            logger.warn("Öğretmen bulunamadı. ID: {}, Hata: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Öğretmen bilgileri alınırken beklenmeyen hata oluştu. ID: {}", id, e);
            return ResponseEntity.internalServerError().body("Öğretmen bilgileri alınırken bir hata oluştu: " + e.getMessage());
        }
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<?> addTeacher(@RequestBody TeacherDto teacherDto) {
        try {
            logger.info("Yeni öğretmen ekleniyor: {}", teacherDto.getName());
            Teacher savedTeacher = teacherService.saveTeacherFromDto(teacherDto);
            logger.info("Öğretmen başarıyla eklendi: {}", savedTeacher.getName());
            return ResponseEntity.ok(teacherService.convertToDto(savedTeacher));
        } catch (RuntimeException e) {
            logger.warn("Öğretmen eklenirken validasyon hatası: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Öğretmen eklenirken beklenmeyen hata oluştu", e);
            return ResponseEntity.internalServerError().body("Öğretmen eklenirken bir hata oluştu: " + e.getMessage());
        }
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> updateTeacher(@PathVariable Long id, @RequestBody TeacherDto teacherDto) {
        try {
            logger.info("Öğretmen güncelleniyor. ID: {}", id);
            if (!teacherService.getTeacherById(id).isPresent()) {
                logger.warn("Güncellenecek öğretmen bulunamadı. ID: {}", id);
                return ResponseEntity.badRequest().body("Öğretmen bulunamadı!");
            }
            teacherDto.setId(id);
            Teacher updatedTeacher = teacherService.saveTeacherFromDto(teacherDto);
            logger.info("Öğretmen başarıyla güncellendi: {}", updatedTeacher.getName());
            return ResponseEntity.ok(teacherService.convertToDto(updatedTeacher));
        } catch (RuntimeException e) {
            logger.warn("Öğretmen güncellenirken validasyon hatası: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Öğretmen güncellenirken beklenmeyen hata oluştu. ID: {}", id, e);
            return ResponseEntity.internalServerError().body("Öğretmen güncellenirken bir hata oluştu: " + e.getMessage());
        }
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteTeacher(@PathVariable Long id) {
        try {
            logger.info("Öğretmen siliniyor. ID: {}", id);
            teacherService.deleteTeacher(id);
            logger.info("Öğretmen başarıyla silindi. ID: {}", id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            logger.warn("Öğretmen silinirken validasyon hatası: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Öğretmen silinirken beklenmeyen hata oluştu. ID: {}", id, e);
            return ResponseEntity.internalServerError().body("Öğretmen silinirken bir hata oluştu: " + e.getMessage());
        }
    }

    @GetMapping("/api/last-teacher-number")
    @ResponseBody
    public ResponseEntity<String> getLastTeacherNumber() {
        try {
            String lastNumber = teacherService.getLastTeacherNumber();
            logger.info("Son öğretmen numarası alındı: {}", lastNumber);
            return ResponseEntity.ok(lastNumber);
        } catch (Exception e) {
            logger.error("Son öğretmen numarası alınırken hata oluştu", e);
            return ResponseEntity.internalServerError().build();
        }
    }
} 