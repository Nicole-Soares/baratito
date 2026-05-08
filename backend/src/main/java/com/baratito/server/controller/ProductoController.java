package com.baratito.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductoController {

    // GET /api/productos/buscar?nombre=leche
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarProductos(@RequestParam String nombre) {

        // Validación: nombre vacío o solo espacios
        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Ingrese un producto"));
        }

        String nombreLower = nombre.trim().toLowerCase();

        // TODO: Aca se lo llamaria al service para conseguir los datos para la res
        List<Map<String, Object>> resultados = PRODUCTOS_MOCK.stream()
                .filter(p -> p.get("nombre").toString().toLowerCase().contains(nombreLower))
                .toList();

        return ResponseEntity.ok(Map.of(
                "resultados", resultados,
                "total", resultados.size(),
                "busqueda", nombre.trim()
        ));
    }



    // TODO: Datos de prueba hardcodeados hasta tener los scrappers o BD real
    private static final List<Map<String, Object>> PRODUCTOS_MOCK = List.of(
            Map.of("nombre", "Leche Entera La Serenísima (1L)", "categoria", "Lácteos",
                    "precios", List.of(
                            Map.of("supermercado", "Carrefour", "precio", 1320),
                            Map.of("supermercado", "Coto", "precio", 1190)
                    )),
            Map.of("nombre", "Arroz Largo Fino (1kg)", "categoria", "Almacén",
                    "precios", List.of(
                            Map.of("supermercado", "Coto", "precio", 890),
                            Map.of("supermercado", "Día", "precio", 950)
                    )),
            Map.of("nombre", "Yerba Mate Taragüí (500g)", "categoria", "Infusiones",
                    "precios", List.of(
                            Map.of("supermercado", "Día", "precio", 1550),
                            Map.of("supermercado", "Carrefour", "precio", 1600)
                    )),
            Map.of("nombre", "Pan Lactal Grande", "categoria", "Panadería",
                    "precios", List.of(
                            Map.of("supermercado", "Carrefour", "precio", 980),
                            Map.of("supermercado", "Changomas", "precio", 870)
                    )),
            Map.of("nombre", "Fideos Spaghetti (500g)", "categoria", "Almacén",
                    "precios", List.of(
                            Map.of("supermercado", "Changomas", "precio", 710),
                            Map.of("supermercado", "Coto", "precio", 780)
                    )),
            Map.of("nombre", "Aceite de Girasol (1.5L)", "categoria", "Almacén",
                    "precios", List.of(
                            Map.of("supermercado", "Día", "precio", 2100),
                            Map.of("supermercado", "Carrefour", "precio", 2250)
                    )),
            Map.of("nombre", "Azúcar (1kg)", "categoria", "Almacén",
                    "precios", List.of(
                            Map.of("supermercado", "Coto", "precio", 650),
                            Map.of("supermercado", "Changomas", "precio", 620)
                    )),
            Map.of("nombre", "Queso Cremoso (400g)", "categoria", "Lácteos",
                    "precios", List.of(
                            Map.of("supermercado", "Carrefour", "precio", 1800),
                            Map.of("supermercado", "Coto", "precio", 1750)
                    ))
    );

}