package com.baratito.server.controller;

import com.baratito.server.model.Notificacion;
import com.baratito.server.service.NotificacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    // GET /api/notificaciones -> lista completa, para pintar la pantalla de Notifications.jsx
    @GetMapping
    public ResponseEntity<List<Notificacion>> obtenerNotificaciones(Authentication authentication) {
        Long usuarioId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(notificacionService.obtenerNotificaciones(usuarioId));
    }

    // GET /api/notificaciones/no-leidas/count -> para el numerito del badge en el ícono 🔔
    @GetMapping("/no-leidas/count")
    public ResponseEntity<Map<String, Long>> contarNoLeidas(Authentication authentication) {
        Long usuarioId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(Map.of("count", notificacionService.contarNoLeidas(usuarioId)));
    }

    // PUT /api/notificaciones/marcar-leidas -> se llama al entrar a la pantalla, para limpiar el badge
    @PutMapping("/marcar-leidas")
    public ResponseEntity<Map<String, String>> marcarTodasComoLeidas(Authentication authentication) {
        Long usuarioId = (Long) authentication.getPrincipal();
        notificacionService.marcarTodasComoLeidas(usuarioId);
        return ResponseEntity.ok(Map.of("message", "Notificaciones marcadas como leídas"));
    }
}