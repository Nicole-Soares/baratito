package com.baratito.server.persistence.sql;
import com.baratito.server.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioSQLDAO extends JpaRepository<Usuario, Long> {

     boolean existsByEmail(String email);

     Usuario findByEmail(String email);

     Optional<Usuario> findById(Long id);
}

