package com.baratito.server.persistence.impl;

import com.baratito.server.persistence.sql.HistorialCarritoSQLDAO;
import com.baratito.server.model.HistorialCarrito;
import com.baratito.server.persistence.interfaces.HistorialCarritoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HistorialCarritoRepositoryImpl implements HistorialCarritoRepository {

    private final HistorialCarritoSQLDAO historialCarritoSQLDAO;

    public HistorialCarritoRepositoryImpl(
            HistorialCarritoSQLDAO historialProductoSQLDAO
    ) {
        this.historialCarritoSQLDAO = historialProductoSQLDAO;
    }

    @Override
    public List<HistorialCarrito> findByUsuarioId(Long usuarioId) {
        return historialCarritoSQLDAO.findByUsuario_Id(usuarioId);
    }

    @Override
    public boolean existsByUsuarioIdAndProductoId(
            Long usuarioId,
            Long productoId
    ) {
        return historialCarritoSQLDAO
                .existsByUsuario_IdAndProductoId(
                        usuarioId,
                        productoId
                );
    }

    @Override
    public HistorialCarrito save(
            HistorialCarrito historialProducto
    ) {
        return historialCarritoSQLDAO.save(historialProducto);
    }
}
