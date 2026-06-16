package com.baratito.server.persistence.sql;

import com.baratito.server.model.HistorialCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistorialCarritoSQLDAO extends JpaRepository<HistorialCarrito, Long> {

    List<HistorialCarrito> findByUsuario_Id(Long usuarioId);

    boolean existsByUsuario_IdAndProductoId(
            Long usuarioId,
            Long productoId
    );
}
