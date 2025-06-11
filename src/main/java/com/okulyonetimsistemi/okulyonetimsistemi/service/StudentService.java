package com.okulyonetimsistemi.okulyonetimsistemi.service;

import com.okulyonetimsistemi.okulyonetimsistemi.model.ClassEntity;
import com.okulyonetimsistemi.okulyonetimsistemi.model.Student;
import com.okulyonetimsistemi.okulyonetimsistemi.repository.ClassRepository;
import com.okulyonetimsistemi.okulyonetimsistemi.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassRepository classRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student saveStudent(Student student) {
        // Sınıf ilişkilendirme
        if (student.getClassEntity() != null && student.getClassEntity().getGrade() != null && student.getClassEntity().getSection() != null) {
            try {
                String className = student.getClassEntity().getGrade() + "-" + student.getClassEntity().getSection();
                ClassEntity classEntity = classRepository.findByName(className)
                    .orElseThrow(() -> new RuntimeException("Belirtilen sınıf bulunamadı: " + className));
                student.setClassEntity(classEntity);
            } catch (RuntimeException e) {
                throw e; // Sınıf bulunamadı hatasını yeniden fırlat
            } catch (Exception e) {
                throw new RuntimeException("Sınıf atanırken bir hata oluştu: " + e.getMessage());
            }
        } else if (student.getClassEntity() != null) {
            // Sınıf nesnesi var ama grade veya section eksikse
            throw new RuntimeException("Sınıf derecesi veya şube bilgisi eksik.");
        } else {
            // Eğer sınıf bilgisi hiç yoksa
            student.setClassEntity(null); // veya duruma göre bir hata fırlatılabilir
        }

        // Öğrenci numarası oluştur
        String newStudentNumber;
        try {
            String lastStudentNumber = studentRepository.findTopByOrderByStudentNumberDesc()
                .map(Student::getStudentNumber)
                .orElse("ogr00");

            int lastNumber = 0;
            if (lastStudentNumber.length() > 3) {
                try {
                    lastNumber = Integer.parseInt(lastStudentNumber.substring(3));
                } catch (NumberFormatException e) {
                    lastNumber = 0; // Geçersiz numara formatı durumunda başlangıç numarası olarak 0 kullan
                }
            }
            
            newStudentNumber = String.format("ogr%02d", lastNumber + 1);

            // Oluşturulan öğrenci numarasının benzersizliğini kontrol et
            if (studentRepository.existsByStudentNumber(newStudentNumber)) {
                throw new RuntimeException("Oluşturulan öğrenci numarası zaten mevcut. Lütfen tekrar deneyin.");
            }

            student.setStudentNumber(newStudentNumber);
        } catch (RuntimeException e) {
            throw e; // Benzersizlik hatasını yeniden fırlat
        } catch (Exception e) {
            throw new RuntimeException("Öğrenci numarası oluşturulurken hata oluştu: " + e.getMessage());
        }

        // Öğrenciyi kaydet
        try {
            return studentRepository.save(student);
        } catch (Exception e) {
            throw new RuntimeException("Öğrenci veritabanına kaydedilirken hata oluştu: " + e.getMessage());
        }
    }

    public Student updateStudent(Student student) {
        try {
            Student existingStudent = studentRepository.findById(student.getId())
                .orElseThrow(() -> new RuntimeException("Öğrenci bulunamadı!"));

            existingStudent.setName(student.getName());
            existingStudent.setParentPhone(student.getParentPhone());

            if (student.getClassEntity() != null && student.getClassEntity().getGrade() != null && student.getClassEntity().getSection() != null) {
                String className = student.getClassEntity().getGrade() + "-" + student.getClassEntity().getSection();
                
                ClassEntity classToAssociate;
                Optional<ClassEntity> foundClass = classRepository.findByName(className);
                
                if (foundClass.isPresent()) {
                    classToAssociate = foundClass.get();
                    // Var olan sınıfın kapasitesini kontrol et ve ayarla
                    if (classToAssociate.getCapacity() == null || classToAssociate.getCapacity() < 1) {
                        classToAssociate.setCapacity(30); // Varsayılan kapasite
                        classRepository.save(classToAssociate); // Güncellenmiş sınıfı kaydet
                    }
                } else {
                    // Yeni sınıf oluştur ve kaydet
                    ClassEntity newClass = new ClassEntity();
                    newClass.setName(className);
                    newClass.setGrade(student.getClassEntity().getGrade());
                    newClass.setSection(student.getClassEntity().getSection());
                    newClass.setCapacity(30); // Varsayılan kapasiteyi ayarla
                    
                    // Yeni sınıfı kaydet ve yönetilen entity'yi al
                    classToAssociate = classRepository.save(newClass);
                }
                existingStudent.setClassEntity(classToAssociate);
            } else {
                existingStudent.setClassEntity(null);
            }

            return studentRepository.save(existingStudent);
        } catch (Exception e) {
            throw new RuntimeException("Öğrenci güncellenirken bir hata oluştu: " + e.getMessage());
        }
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return studentRepository.existsByName(name);
    }
}