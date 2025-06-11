package com.okulyonetimsistemi.okulyonetimsistemi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testDatabaseConnection() {
        assertNotNull(dataSource, "Veritabanı bağlantısı başarısız!");
        assertNotNull(jdbcTemplate, "JdbcTemplate oluşturulamadı!");
        
        // Basit bir sorgu çalıştır
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertNotNull(result, "Veritabanı sorgusu çalıştırılamadı!");
    }
} 