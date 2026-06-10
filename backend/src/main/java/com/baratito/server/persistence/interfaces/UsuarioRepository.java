package com.baratito.server.persistence.interfaces;

import com.baratito.server.model.Usuario;

public interface UsuarioRepository {
    boolean existsByEmail(String email);

    Usuario save(Usuario usuario);

    Usuario findByEmail(String email);
}
