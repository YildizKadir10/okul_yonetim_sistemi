package com.okulyonetimsistemi.okulyonetimsistemi.dto;

import lombok.Data;

@Data
public class TeacherDto {
    private Long id;
    private String name;
    private String subject;
    private String teacherNumber;
    private String email;

    // Constructors
    public TeacherDto() {
    }

    public TeacherDto(Long id, String name, String subject, String teacherNumber, String email) {
        this.id = id;
        this.name = name;
        this.subject = subject;
        this.teacherNumber = teacherNumber;
        this.email = email;
    }

    // Getters and Setters (Lombok @Data handles this, but explicitly for clarity if Lombok is not used)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTeacherNumber() {
        return teacherNumber;
    }

    public void setTeacherNumber(String teacherNumber) {
        this.teacherNumber = teacherNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
} 