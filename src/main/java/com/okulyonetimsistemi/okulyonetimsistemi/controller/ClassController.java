package com.okulyonetimsistemi.okulyonetimsistemi.controller;

import com.okulyonetimsistemi.okulyonetimsistemi.model.ClassEntity;
import com.okulyonetimsistemi.okulyonetimsistemi.model.Teacher;
import com.okulyonetimsistemi.okulyonetimsistemi.service.ClassService;
import com.okulyonetimsistemi.okulyonetimsistemi.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/classes")
public class ClassController {

    @Autowired
    private ClassService classService;

    @Autowired
    private TeacherService teacherService;

    @GetMapping
    public String listClasses(Model model) {
        List<ClassEntity> classes = classService.getAllClasses();
        List<Teacher> teachers = teacherService.getAllTeachers();
        model.addAttribute("classes", classes);
        model.addAttribute("teachers", teachers);
        return "classes";
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<ClassEntity> createClass(@RequestBody ClassEntity classEntity) {
        return ResponseEntity.ok(classService.saveClass(classEntity));
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<ClassEntity> getClassById(@PathVariable Long id) {
        return ResponseEntity.ok(classService.getClassById(id));
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<ClassEntity> updateClass(@PathVariable Long id, @RequestBody ClassEntity classEntity) {
        classEntity.setId(id);
        return ResponseEntity.ok(classService.updateClass(classEntity));
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
        return ResponseEntity.ok().build();
    }
} 