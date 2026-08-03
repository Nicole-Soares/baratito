package com.baratito.server.persistence.interfaces;

import com.baratito.server.model.Usuario;

import java.util.Optional;

public interface UsuarioRepository {
    boolean existsByEmail(String email);

    Usuario save(Usuario usuario);

    Usuario findByEmail(String email);

    Optional<Usuario> findById(Long id);
}
