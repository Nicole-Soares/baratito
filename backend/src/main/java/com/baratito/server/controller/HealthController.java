package com.baratito.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Instant;
import java.util.Map;

@RestController
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        String dbStatus;
        try (Connection conn = dataSource.getConnection()) {
            conn.isValid(2); // timeout de 2 segundos
            dbStatus = "UP";
        } catch (Exception e) {
            dbStatus = "DOWN";
        }

        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "db", dbStatus,
                "timestamp", Instant.now().toString()
        ));
    }
}

