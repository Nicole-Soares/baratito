package com.baratito.server.controller;

import com.baratito.server.service.CarritoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping
    public ResponseEntity<?> obtenerCarrito(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(Map.of(
                "productos", carritoService.getItems(userId),
                "total", carritoService.getTotal(userId)
        ));
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> agregar(@PathVariable Long id, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        carritoService.agregar(userId, id);
        return ResponseEntity.ok(Map.of(
                "productos", carritoService.getItems(userId),
                "total", carritoService.getTotal(userId)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> decrementar(@PathVariable Long id, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        carritoService.decrementar(userId, id);
        return ResponseEntity.ok(Map.of(
                "productos", carritoService.getItems(userId),
                "total", carritoService.getTotal(userId)
        ));
    }
}