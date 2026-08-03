package com.baratito.server.persistence.impl;

import com.baratito.server.model.Usuario;
import com.baratito.server.persistence.interfaces.UsuarioRepository;
import com.baratito.server.persistence.sql.UsuarioSQLDAO;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final UsuarioSQLDAO usuarioSQLDAO;

    public UsuarioRepositoryImpl(UsuarioSQLDAO usuarioSQLDAO) {
        this.usuarioSQLDAO = usuarioSQLDAO;
    }

    @Override
    public boolean existsByEmail(String email) {
        return usuarioSQLDAO.existsByEmail(email);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return this.usuarioSQLDAO.save(usuario);
    }

    @Override
    public Usuario findByEmail(String email) {
        return this.usuarioSQLDAO.findByEmail(email);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return this.usuarioSQLDAO.findById(id);
    }

}
