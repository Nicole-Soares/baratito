package com.baratito.server.persistence.interfaces;

import com.baratito.server.model.CarritoItem;

import java.util.List;
import java.util.Optional;

public interface CarritoRepository {
    Optional<CarritoItem> findById(Long productoId);

    List<CarritoItem> findAll();

    void save(CarritoItem item);

    void delete(CarritoItem item);

    void deleteAll();

    List<CarritoItem> findByUsuarioId(Long usuarioId);

    Optional<CarritoItem> findByUsuarioIdAndProductoId(
            Long usuarioId,
            Long productoId
    );

    Optional<CarritoItem> findByProductoId(Long productoId);
}
