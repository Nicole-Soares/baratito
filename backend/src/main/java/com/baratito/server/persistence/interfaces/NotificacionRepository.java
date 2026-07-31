package com.baratito.server.persistence.interfaces;

import com.baratito.server.model.Notificacion;

import java.util.List;

public interface NotificacionRepository {
    void save(Notificacion notificacion);

    List<Notificacion> findByUsuarioIdOrderByCreadaDesc(Long usuarioId);

    long countByUsuarioIdAndLeidaFalse(Long usuarioId);

    void marcarTodasComoLeidas(Long usuarioId);
}
