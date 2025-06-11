package com.okulyonetimsistemi.okulyonetimsistemi.service;

import com.okulyonetimsistemi.okulyonetimsistemi.dto.TeacherDto;
import com.okulyonetimsistemi.okulyonetimsistemi.model.Teacher;
import com.okulyonetimsistemi.okulyonetimsistemi.repository.TeacherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeacherService {
    private static final Logger logger = LoggerFactory.getLogger(TeacherService.class);

    @Autowired
    private TeacherRepository teacherRepository;

    public List<Teacher> getAllTeachers() {
        try {
            return teacherRepository.findAll();
        } catch (Exception e) {
            logger.error("Tüm öğretmenler alınırken hata oluştu", e);
            throw new RuntimeException("Öğretmenler alınırken bir hata oluştu: " + e.getMessage());
        }
    }

    public Optional<TeacherDto> getTeacherById(Long id) {
        try {
            logger.debug("Öğretmen aranıyor. ID: {}", id);
            return teacherRepository.findById(id)
                    .map(this::convertToDto);
        } catch (Exception e) {
            logger.error("Öğretmen aranırken hata oluştu. ID: {}", id, e);
            throw new RuntimeException("Öğretmen bilgileri alınırken bir hata oluştu: " + e.getMessage());
        }
    }

    @Transactional
    public Teacher saveTeacher(Teacher teacher) {
        try {
            logger.debug("Öğretmen kaydediliyor: {}", teacher.getName());
            
            Teacher teacherToSave;

            if (teacher.getId() == null) { // Yeni öğretmen ekleniyorsa
                logger.debug("Yeni öğretmen ekleniyor");
                teacherToSave = new Teacher();
                teacherToSave.setName(teacher.getName());
                teacherToSave.setSubject(teacher.getSubject());
                teacherToSave.setEmail(teacher.getEmail());
                
                // Öğretmen numarası oluştur
                String lastTeacherNumber = teacherRepository.findTopByOrderByTeacherNumberDesc()
                    .map(Teacher::getTeacherNumber)
                    .orElse("T000");

                int lastNumber = Integer.parseInt(lastTeacherNumber.substring(1)); // "T" karakterini atla
                String newTeacherNumber = String.format("T%03d", lastNumber + 1);
                teacherToSave.setTeacherNumber(newTeacherNumber);
                logger.debug("Yeni öğretmen numarası oluşturuldu: {}", newTeacherNumber);

                // E-posta kontrolü
                if (teacherToSave.getEmail() != null && teacherRepository.existsByEmail(teacherToSave.getEmail())) {
                    logger.warn("E-posta adresi zaten kullanılıyor: {}", teacherToSave.getEmail());
                    throw new RuntimeException("Bu e-posta adresi zaten kullanılıyor.");
                }
            } else { // Mevcut öğretmen güncelleniyorsa
                logger.debug("Mevcut öğretmen güncelleniyor. ID: {}", teacher.getId());
                teacherToSave = teacherRepository.findById(teacher.getId())
                    .orElseThrow(() -> {
                        String errorMessage = "Öğretmen bulunamadı.";
                        logger.warn("Güncellenecek öğretmen bulunamadı. ID: {}", teacher.getId());
                        return new RuntimeException(errorMessage);
                    });

                // E-posta değişmişse ve başka bir öğretmen tarafından kullanılıyorsa kontrol et
                if (!teacherToSave.getEmail().equals(teacher.getEmail()) && 
                    teacherRepository.existsByEmail(teacher.getEmail())) {
                    logger.warn("E-posta adresi zaten kullanılıyor: {}", teacher.getEmail());
                    throw new RuntimeException("Bu e-posta adresi zaten kullanılıyor.");
                }
                
                // Alanları güncelle
                teacherToSave.setName(teacher.getName());
                teacherToSave.setSubject(teacher.getSubject());
                teacherToSave.setEmail(teacher.getEmail());

                // Öğretmen numarasını koru
                // teacherToSave.setTeacherNumber(teacherToSave.getTeacherNumber()); // Zaten mevcut olan numarayı koruyor

                logger.debug("Öğretmen bilgileri güncellendi: {}", teacherToSave.getName());
            }

            // Ad Soyad ve Branş kontrolü
            if (teacherToSave.getName() == null || teacherToSave.getName().trim().isEmpty()) {
                logger.warn("Ad Soyad alanı boş bırakılamaz");
                throw new RuntimeException("Ad Soyad alanı boş bırakılamaz.");
            }
            if (teacherToSave.getSubject() == null || teacherToSave.getSubject().trim().isEmpty()) {
                logger.warn("Branş alanı boş bırakılamaz");
                throw new RuntimeException("Branş alanı boş bırakılamaz.");
            }

            Teacher savedTeacher = teacherRepository.save(teacherToSave);
            logger.info("Öğretmen başarıyla kaydedildi: {}", savedTeacher.getName());
            return savedTeacher;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Öğretmen kaydedilirken beklenmeyen hata oluştu", e);
            throw new RuntimeException("Öğretmen kaydedilirken bir hata oluştu: " + e.getMessage());
        }
    }

    public Teacher saveTeacherFromDto(TeacherDto teacherDto) {
        try {
            logger.debug("Öğretmen DTO'dan kaydediliyor/güncelleniyor. ID: {}", teacherDto.getId());
            Teacher teacher;
            if (teacherDto.getId() == null) {
                // Yeni öğretmen oluşturuluyor
                teacher = new Teacher();
                teacher.setName(teacherDto.getName());
                teacher.setSubject(teacherDto.getSubject());
                teacher.setTeacherNumber(teacherDto.getTeacherNumber()); // DTO'dan gelen numarayı kullan
                teacher.setEmail(teacherDto.getEmail());
            } else {
                // Mevcut öğretmen güncelleniyor
                teacher = teacherRepository.findById(teacherDto.getId())
                        .orElseThrow(() -> new RuntimeException("Güncellenecek öğretmen bulunamadı."));
                teacher.setName(teacherDto.getName());
                teacher.setSubject(teacherDto.getSubject());
                // teacherNumber ve email zaten DTO'dan okunuyor ve değiştirilmiyor (readonly)
                teacher.setEmail(teacherDto.getEmail());
            }

            // Validasyonlar
            if (teacher.getName() == null || teacher.getName().trim().isEmpty()) {
                throw new RuntimeException("Ad Soyad alanı boş bırakılamaz.");
            }
            if (teacher.getSubject() == null || teacher.getSubject().trim().isEmpty()) {
                throw new RuntimeException("Branş alanı boş bırakılamaz.");
            }
            if (teacher.getEmail() == null || teacher.getEmail().trim().isEmpty()) {
                throw new RuntimeException("E-posta alanı boş bırakılamaz.");
            }

            // E-posta ve öğretmen numarası benzersizlik kontrolü (sadece yeni ekleme veya e-posta değişimi için)
            if (teacherDto.getId() == null) {
                if (teacherRepository.existsByEmail(teacher.getEmail())) {
                    throw new RuntimeException("Bu e-posta adresi zaten kullanılıyor.");
                }
                if (teacherRepository.existsByTeacherNumber(teacher.getTeacherNumber())) {
                    throw new RuntimeException("Bu öğretmen numarası zaten kullanılıyor.");
                }
            } else {
                // Güncelleme durumunda e-posta değişmişse kontrol et
                Optional<Teacher> existingTeacher = teacherRepository.findById(teacherDto.getId());
                if (existingTeacher.isPresent() && !existingTeacher.get().getEmail().equals(teacherDto.getEmail())) {
                    if (teacherRepository.existsByEmail(teacherDto.getEmail())) {
                        throw new RuntimeException("Bu e-posta adresi zaten kullanılıyor.");
                    }
                }
            }

            Teacher savedTeacher = teacherRepository.save(teacher);
            logger.info("Öğretmen DTO'dan başarıyla kaydedildi: {}", savedTeacher.getName());
            return savedTeacher;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Öğretmen DTO'dan kaydedilirken beklenmeyen hata oluştu", e);
            throw new RuntimeException("Öğretmen DTO'dan kaydedilirken bir hata oluştu: " + e.getMessage());
        }
    }

    public List<TeacherDto> getAllTeacherDtos() {
        return teacherRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteTeacher(Long id) {
        try {
            logger.debug("Öğretmen siliniyor. ID: {}", id);
            Teacher teacher = teacherRepository.findById(id)
                    .orElseThrow(() -> {
                        String errorMessage = "Öğretmen bulunamadı.";
                        logger.warn("Silinecek öğretmen bulunamadı. ID: {}", id);
                        return new RuntimeException(errorMessage);
                    });
            teacherRepository.delete(teacher);
            logger.info("Öğretmen başarıyla silindi: {}", teacher.getName());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Öğretmen silinirken beklenmeyen hata oluştu. ID: {}", id, e);
            throw new RuntimeException("Öğretmen silinirken bir hata oluştu: " + e.getMessage());
        }
    }

    public List<Teacher> findBySubject(String subject) {
        try {
            return teacherRepository.findBySubject(subject);
        } catch (Exception e) {
            logger.error("Branşa göre öğretmenler aranırken hata oluştu. Branş: {}", subject, e);
            throw new RuntimeException("Öğretmenler aranırken bir hata oluştu: " + e.getMessage());
        }
    }

    public boolean existsByEmail(String email) {
        try {
            return teacherRepository.existsByEmail(email);
        } catch (Exception e) {
            logger.error("E-posta kontrolü yapılırken hata oluştu. E-posta: {}", email, e);
            throw new RuntimeException("E-posta kontrolü yapılırken bir hata oluştu: " + e.getMessage());
        }
    }

    public String getLastTeacherNumber() {
        try {
            String lastNumber = teacherRepository.findTopByOrderByTeacherNumberDesc()
                    .map(Teacher::getTeacherNumber)
                    .orElse("T000");
            logger.debug("Son öğretmen numarası alındı: {}", lastNumber);
            return lastNumber;
        } catch (Exception e) {
            logger.error("Son öğretmen numarası alınırken hata oluştu", e);
            throw new RuntimeException("Son öğretmen numarası alınırken bir hata oluştu: " + e.getMessage());
        }
    }

    public TeacherDto convertToDto(Teacher teacher) {
        return new TeacherDto(teacher.getId(), teacher.getName(), teacher.getSubject(), teacher.getTeacherNumber(), teacher.getEmail());
    }

    private Teacher convertToEntity(TeacherDto teacherDto) {
        Teacher teacher = new Teacher();
        teacher.setId(teacherDto.getId());
        teacher.setName(teacherDto.getName());
        teacher.setSubject(teacherDto.getSubject());
        teacher.setTeacherNumber(teacherDto.getTeacherNumber());
        teacher.setEmail(teacherDto.getEmail());
        return teacher;
    }
} 