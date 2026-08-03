package com.baratito.server.persistence.sql;
import com.baratito.server.model.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarritoSQLDAO extends JpaRepository<CarritoItem, Long> {
    Optional<CarritoItem> findByProductoId(Long productoId);

    Optional<CarritoItem> findByUsuarioIdAndProductoId(Long usuarioId, Long productoId);

    List<CarritoItem> findByUsuarioId(Long usuarioId);
}
