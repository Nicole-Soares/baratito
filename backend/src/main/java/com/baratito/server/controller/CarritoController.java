package com.baratito.server.controller;

import com.baratito.server.service.CarritoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
@CrossOrigin(origins = "*")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping
    public ResponseEntity<?> obtenerCarrito() {
        return ResponseEntity.ok(Map.of(
                "productos", carritoService.getItems(),
                "total", carritoService.getTotal()
        ));
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> agregar(@PathVariable Long id) {
        try {
            carritoService.agregar(id);
            return ResponseEntity.ok(Map.of(
                    "productos", carritoService.getItems(),
                    "total", carritoService.getTotal()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> decrementar(@PathVariable Long id) {
        carritoService.decrementar(id);
        return ResponseEntity.ok(Map.of(
                "productos", carritoService.getItems(),
                "total", carritoService.getTotal()
        ));
    }



}