package com.baratito.server.service;

import com.baratito.server.model.ProductoSchema;
import com.baratito.server.model.Usuario;
import com.baratito.server.persistence.interfaces.UsuarioRepository;
import com.baratito.server.persistence.sql.ProductoSQLDAO;
import com.baratito.server.persistence.sql.UsuarioSQLDAO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class FavoritoService {

    private final UsuarioSQLDAO usuarioSQLDAO;
    private final ProductoSQLDAO productoSQLDAO;

    public FavoritoService(UsuarioSQLDAO usuarioSQLDAO, ProductoSQLDAO productoSQLDAO) {
        this.usuarioSQLDAO = usuarioSQLDAO;
        this.productoSQLDAO = productoSQLDAO;
    }

    @Transactional
    public void agregarFavorito(Long usuarioId, Long productoId) {
        Usuario usuario = usuarioSQLDAO.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        ProductoSchema producto = productoSQLDAO.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        usuario.getFavoritos().add(producto);
        usuarioSQLDAO.save(usuario);
    }

    @Transactional
    public void quitarFavorito(Long usuarioId, Long productoId) {
        Usuario usuario = usuarioSQLDAO.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        ProductoSchema producto = productoSQLDAO.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        usuario.getFavoritos().remove(producto);
        usuarioSQLDAO.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<ProductoSchema> obtenerFavoritos(Long usuarioId) {
        Usuario usuario = usuarioSQLDAO.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return List.copyOf(usuario.getFavoritos());
    }

    @Transactional(readOnly = true)
    public boolean esFavorito(Long usuarioId, Long productoId) {
        Usuario usuario = usuarioSQLDAO.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return usuario.getFavoritos().stream()
                .anyMatch(p -> p.getId().equals(productoId));
    }
}
