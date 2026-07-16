package com.baratito.server.controller;

import com.baratito.server.controller.dto.favorito.FavoritoDTO;
import com.baratito.server.model.ProductoSchema;
import com.baratito.server.service.FavoritoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favoritos")
@CrossOrigin(origins = "*", exposedHeaders = "Authorization")
public class FavoritoController {

    private final FavoritoService favoritoService;

    public FavoritoController(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    private Long getUsuarioId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();

        // Debug: Imprime esto en tu consola de servidor
        System.out.println("Tipo de principal: " + principal.getClass().getName());
        System.out.println("Valor de principal: " + principal.toString());

        // Si tu principal es un objeto, aquí es donde suele estar el fallo.
        // Si usas un JWT, es probable que necesites extraer el ID de otra forma.
        return (Long) principal;
    }

    @GetMapping
    public ResponseEntity<List<FavoritoDTO>> obtenerFavoritos() {
        Long usuarioId = getUsuarioId();
        List<ProductoSchema> favoritos = favoritoService.obtenerFavoritos(usuarioId);

        List<FavoritoDTO> dto = favoritos.stream()
                .map(p -> new FavoritoDTO(p.getId(), p.getNombre(), p.getSource(), p.getImagen(),
                        p.getPrecio(), p.getPrecioLista(), p.getLink()))
                .toList();

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/check/{productoId}")
    public ResponseEntity<Map<String, Boolean>> esFavorito(@PathVariable Long productoId) {
        Long usuarioId = getUsuarioId();
        boolean esFav = favoritoService.esFavorito(usuarioId, productoId);
        return ResponseEntity.ok(Map.of("esFavorito", esFav));
    }

    @PostMapping("/{productoId}")
    public ResponseEntity<Map<String, String>> agregarFavorito(@PathVariable Long productoId) {
        Long usuarioId = getUsuarioId();
        favoritoService.agregarFavorito(usuarioId, productoId);
        return ResponseEntity.ok(Map.of("mensaje", "Producto agregado a favoritos"));
    }

    @DeleteMapping("/{productoId}")
    public ResponseEntity<Map<String, String>> quitarFavorito(@PathVariable Long productoId) {
        Long usuarioId = getUsuarioId();
        favoritoService.quitarFavorito(usuarioId, productoId);
        return ResponseEntity.ok(Map.of("mensaje", "Producto eliminado de favoritos"));
    }
}
