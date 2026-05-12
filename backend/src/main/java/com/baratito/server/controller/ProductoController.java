package com.baratito.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
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
                .sorted(Comparator.comparingInt(p -> (Integer) p.get("precio")))
                .toList();

        return ResponseEntity.ok(Map.of(
                "resultados", resultados,
                "total", resultados.size(),
                "busqueda", nombre.trim()
        ));
    }



    // TODO: Datos de prueba hardcodeados hasta tener los scrappers o BD real
    private static final List<Map<String, Object>> PRODUCTOS_MOCK = List.of(
        Map.of(
                "nombre", "Leche Entera La Serenísima (1L)",
                "categoria", "Lácteos",
                "supermercado", "Coto",
                "precio", 1190,
                "actualizado", "2026-05-01T11:00:00Z"
        ),
        Map.of(
                "nombre", "Leche Entera La Serenísima (1L)",
                "categoria", "Lácteos",
                "supermercado", "Carrefour",
                "precio", 1320,
                "actualizado", "2026-05-10T13:00:00Z"
        ),
        Map.of(
                "nombre", "Leche Entera La Serenísima (1L)",
                "categoria", "Lácteos",
                "supermercado", "Día",
                "precio", 1250,
                "actualizado", "2026-04-25T14:00:00Z"
        ),
        Map.of(
                "nombre", "Leche Entera La Serenísima (1L)",
                "categoria", "Lácteos",
                "supermercado", "Changomas",
                "precio", 1210,
                "actualizado", "2026-05-05T12:00:00Z"
        ),

        Map.of(
                "nombre", "Arroz Largo Fino (1kg)",
                "categoria", "Almacén",
                "supermercado", "Coto",
                "precio", 890,
                "actualizado", "2026-05-01T11:00:00Z"
        ),
        Map.of(
                "nombre", "Arroz Largo Fino (1kg)",
                "categoria", "Almacén",
                "supermercado", "Carrefour",
                "precio", 940,
                "actualizado", "2026-05-10T13:00:00Z"
        ),
        Map.of(
                "nombre", "Arroz Largo Fino (1kg)",
                "categoria", "Almacén",
                "supermercado", "Día",
                "precio", 950,
                "actualizado", "2026-04-25T14:00:00Z"
        ),
        Map.of(
                "nombre", "Arroz Largo Fino (1kg)",
                "categoria", "Almacén",
                "supermercado", "Changomas",
                "precio", 910,
                "actualizado", "2026-05-05T12:00:00Z"
        ),

        Map.of(
                "nombre", "Yerba Mate Taragüí (500g)",
                "categoria", "Infusiones",
                "supermercado", "Coto",
                "precio", 1520,
                "actualizado", "2026-05-01T11:00:00Z"
        ),
        Map.of(
                "nombre", "Yerba Mate Taragüí (500g)",
                "categoria", "Infusiones",
                "supermercado", "Carrefour",
                "precio", 1600,
                "actualizado", "2026-05-10T13:00:00Z"
        ),
        Map.of(
                "nombre", "Yerba Mate Taragüí (500g)",
                "categoria", "Infusiones",
                "supermercado", "Día",
                "precio", 1550,
                "actualizado", "2026-04-25T14:00:00Z"
        ),
        Map.of(
                "nombre", "Yerba Mate Taragüí (500g)",
                "categoria", "Infusiones",
                "supermercado", "Changomas",
                "precio", 1500,
                "actualizado", "2026-05-05T12:00:00Z"
        ),

        Map.of(
                "nombre", "Pan Lactal Grande",
                "categoria", "Panadería",
                "supermercado", "Coto",
                "precio", 910,
                "actualizado", "2026-05-01T15:00:00Z"
        ),
        Map.of(
                "nombre", "Pan Lactal Grande",
                "categoria", "Panadería",
                "supermercado", "Carrefour",
                "precio", 980,
                "actualizado", "2026-05-10T16:00:00Z"
        ),
        Map.of(
                "nombre", "Pan Lactal Grande",
                "categoria", "Panadería",
                "supermercado", "Día",
                "precio", 930,
                "actualizado", "2026-04-25T10:00:00Z"
        ),
        Map.of(
                "nombre", "Pan Lactal Grande",
                "categoria", "Panadería",
                "supermercado", "Changomas",
                "precio", 870,
                "actualizado", "2026-05-05T14:00:00Z"
        ),

        Map.of(
                "nombre", "Fideos Spaghetti (500g)",
                "categoria", "Almacén",
                "supermercado", "Coto",
                "precio", 780,
                "actualizado", "2026-05-01T11:00:00Z"
        ),
        Map.of(
                "nombre", "Fideos Spaghetti (500g)",
                "categoria", "Almacén",
                "supermercado", "Carrefour",
                "precio", 760,
                "actualizado", "2026-05-10T13:00:00Z"
        ),
        Map.of(
                "nombre", "Fideos Spaghetti (500g)",
                "categoria", "Almacén",
                "supermercado", "Día",
                "precio", 740,
                "actualizado", "2026-04-25T14:00:00Z"
        ),
        Map.of(
                "nombre", "Fideos Spaghetti (500g)",
                "categoria", "Almacén",
                "supermercado", "Changomas",
                "precio", 710,
                "actualizado", "2026-05-05T12:00:00Z"
        )
);

}