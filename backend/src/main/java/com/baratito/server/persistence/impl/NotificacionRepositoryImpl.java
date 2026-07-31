package com.baratito.server.persistence.impl;

import com.baratito.server.model.Notificacion;
import com.baratito.server.persistence.interfaces.NotificacionRepository;
import com.baratito.server.persistence.sql.NotificacionSQLDAO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NotificacionRepositoryImpl implements NotificacionRepository {

    private final NotificacionSQLDAO notificacionSQLDAO;

    public NotificacionRepositoryImpl(NotificacionSQLDAO notificacionSQLDAO) {
        this.notificacionSQLDAO = notificacionSQLDAO;
    }


    @Override
    public void save(Notificacion notificacion) {
        notificacionSQLDAO.save(notificacion);
    }

    @Override
    public List<Notificacion> findByUsuarioIdOrderByCreadaDesc(Long usuarioId) {
        return notificacionSQLDAO.findByUsuarioIdOrderByCreadaDesc(usuarioId);
    }

    @Override
    public long countByUsuarioIdAndLeidaFalse(Long usuarioId) {
        return notificacionSQLDAO.countByUsuarioIdAndLeidaFalse(usuarioId);
    }

    @Override
    public void marcarTodasComoLeidas(Long usuarioId) {
        notificacionSQLDAO.marcarTodasComoLeidas(usuarioId);
    }
}
