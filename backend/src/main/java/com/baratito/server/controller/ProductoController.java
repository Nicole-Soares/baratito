package com.baratito.server.controller;

import com.baratito.server.model.ProductoSchema;
import com.baratito.server.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * GET /api/productos/buscar?nombre=leche
     *
     * Busca en todos los supermercados y devuelve resultados ordenados por precio.
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarProductos(@RequestParam String nombre) {

        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Ingrese un producto"));
        }

        List<ProductoSchema> resultados = productoService.buscarProductos(nombre.trim());

        return ResponseEntity.ok(Map.of(
                "resultados", resultados,
                "total",      resultados.size(),
                "busqueda",   nombre.trim()
        ));
    }

    /**
     * GET /api/productos/buscar/agrupado?nombre=leche
     *
     * Igual que /buscar pero agrupa los resultados por supermercado.
     *
     * Respuesta: { "carrefour": [...], "coto": [...] }
     */
    @GetMapping("/buscar/agrupado")
    public ResponseEntity<?> buscarAgrupado(@RequestParam String nombre) {

        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Ingrese un producto"));
        }

        Map<String, List<ProductoSchema>> resultados =
                productoService.buscarProductosAgrupados(nombre.trim());

        return ResponseEntity.ok(resultados);
    }

    /**
     * GET /api/productos/buscar/{supermercado}?nombre=leche
     *
     * Busca solo en un supermercado específico.
     */
    @GetMapping("/buscar/{supermercado}")
    public ResponseEntity<?> buscarEnSupermercado(
            @PathVariable String supermercado,
            @RequestParam String nombre
    ) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Ingrese un producto"));
        }

        List<ProductoSchema> resultados =
                productoService.buscarEnSupermercado(nombre.trim(), supermercado);

        return ResponseEntity.ok(Map.of(
                "resultados",    resultados,
                "total",         resultados.size(),
                "busqueda",      nombre.trim(),
                "supermercado",  supermercado
        ));
    }

    /**
     * GET /api/productos/supermercados
     *
     * Devuelve la lista de supermercados disponibles.
     */
    @GetMapping("/supermercados")
    public ResponseEntity<List<String>> getSupermercados() {
        return ResponseEntity.ok(productoService.getSupermercadosDisponibles());
    }

    @GetMapping("/sugerencias")
    public ResponseEntity<List<String>> obtenerSugerencias(
            @RequestParam String query
    ) {
        return ResponseEntity.ok(
                productoService.obtenerSugerencias(query)
        );
    }
}