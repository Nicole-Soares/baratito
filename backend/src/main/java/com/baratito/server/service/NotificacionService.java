package com.baratito.server.service;

import com.baratito.server.model.Notificacion;
import com.baratito.server.model.ProductoSchema;
import com.baratito.server.model.Usuario;
import com.baratito.server.persistence.interfaces.NotificacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    public void notificarBajadaDePrecio(ProductoSchema producto, double precioAnterior, double precioNuevo) {
        Set<Usuario> interesados = producto.getUsuariosQueLoTienenFavorito();
        if (interesados == null || interesados.isEmpty()) return;

        for (Usuario usuario : interesados) {
            Notificacion notificacion = new Notificacion(
                    usuario.getId(),
                    producto.getId(),
                    producto.getNombre(),
                    producto.getImagen(),
                    precioAnterior,
                    precioNuevo
            );
            notificacionRepository.save(notificacion);
        }
    }

    public List<Notificacion> obtenerNotificaciones(Long usuarioId) {
        return notificacionRepository.findByUsuarioIdOrderByCreadaDesc(usuarioId);
    }

    public long contarNoLeidas(Long usuarioId) {
        return notificacionRepository.countByUsuarioIdAndLeidaFalse(usuarioId);
    }

    @Transactional
    public void marcarTodasComoLeidas(Long usuarioId) {
        notificacionRepository.marcarTodasComoLeidas(usuarioId);
    }
}