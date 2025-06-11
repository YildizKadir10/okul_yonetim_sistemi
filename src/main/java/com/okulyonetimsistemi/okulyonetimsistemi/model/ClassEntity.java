package com.okulyonetimsistemi.okulyonetimsistemi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Data
@Entity
@Table(name = "classes")
public class ClassEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Sınıf adı boş bırakılamaz")
    private String name;

    @NotNull(message = "Kapasite boş bırakılamaz")
    @Min(value = 1, message = "Kapasite en az 1 olmalıdır")
    private Integer capacity;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @OneToMany(mappedBy = "classEntity", fetch = FetchType.LAZY)
    private Set<Student> students = new HashSet<>();

    @Column(name = "grade")
    private Integer grade;

    @Column(name = "section")
    private String section;
} 