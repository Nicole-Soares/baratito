package com.baratito.server.persistence.interfaces;

import com.baratito.server.model.HistorialCarrito;

import java.util.List;

public interface HistorialCarritoRepository {

    List<HistorialCarrito> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndProductoId(
            Long usuarioId,
            Long productoId
    );

    HistorialCarrito save(HistorialCarrito historialProducto);
}
