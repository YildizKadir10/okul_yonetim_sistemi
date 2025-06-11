package com.okulyonetimsistemi.okulyonetimsistemi.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseTest {

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void testConnection() {
        try {
            String result = jdbcTemplate.queryForObject("SELECT version();", String.class);
            System.out.println("Veritabanı bağlantısı başarılı!");
            System.out.println("PostgreSQL Versiyonu: " + result);
        } catch (Exception e) {
            System.err.println("Veritabanı bağlantı hatası!");
            e.printStackTrace();
        }
    }
} 