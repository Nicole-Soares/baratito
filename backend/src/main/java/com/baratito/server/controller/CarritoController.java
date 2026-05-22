package com.baratito.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
@CrossOrigin(origins = "http://localhost:5173")
public class CarritoController {

    private static final List<Map<String, Object>> CARRITO_MOCK = List.of(

            Map.of(
                    "id", 1,
                    "nombre", "Leche Entera La Serenísima (1L)",
                    "source", "Coto",
                    "precio", 1190,
                    "cantidad", 2,
                    "imagen", "https://imagenes.preciosclaros.gob.ar/productos/7793940170005.jpg"
            ),

            Map.of(
                    "id", 2,
                    "nombre", "Yerba Mate Playadito (500g)",
                    "source", "Carrefour",
                    "precio", 1450,
                    "cantidad", 1,
                    "imagen", "https://imagenes.preciosclaros.gob.ar/productos/7793704000230.jpg"
            ),

            Map.of(
                    "id", 3,
                    "nombre", "Fideos Spaghetti (500g)",
                    "source", "Changomas",
                    "precio", 710,
                    "cantidad", 3,
                    "imagen", "https://imagenes.preciosclaros.gob.ar/productos/7790070413729.jpg"
            )
    );

    @GetMapping
    public ResponseEntity<?> obtenerCarrito() {

        double total = CARRITO_MOCK.stream()
                .mapToDouble(item ->
                        ((Integer) item.get("cantidad")) *
                                ((Integer) item.get("precio"))
                )
                .sum();

        return ResponseEntity.ok(Map.of(
                "productos", CARRITO_MOCK,
                "total", total
        ));
    }

}
