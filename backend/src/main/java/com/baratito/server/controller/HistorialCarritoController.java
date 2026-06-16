package com.baratito.server.controller;

import com.baratito.server.service.HistorialCarritoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/historial")
@CrossOrigin(origins = "*")
public class HistorialCarritoController {

    private final HistorialCarritoService historialCarritoService;

    public HistorialCarritoController(
            HistorialCarritoService historialCarritoService
    ) {
        this.historialCarritoService = historialCarritoService;
    }

    @GetMapping
    public ResponseEntity<?> obtenerHistorial() {

        Long usuarioId = Long.parseLong(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal()
                        .toString()
        );

        return ResponseEntity.ok(
                historialCarritoService.obtenerHistorial(usuarioId)
        );
    }
}