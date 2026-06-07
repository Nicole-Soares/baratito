package com.baratito.server.service;

import com.baratito.server.model.HistorialCarrito;
import com.baratito.server.persistence.interfaces.HistorialCarritoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistorialCarritoService {

    private final HistorialCarritoRepository historialCarritoRepository;

    public HistorialCarritoService(
            HistorialCarritoRepository historialCarritoRepository
    ) {
        this.historialCarritoRepository = historialCarritoRepository;
    }

    public List<HistorialCarrito> obtenerHistorial(Long usuarioId) {
        return historialCarritoRepository.findByUsuarioId(usuarioId);
    }

    public boolean existeProducto(
            Long usuarioId,
            Long productoId
    ) {
        return historialCarritoRepository
                .existsByUsuarioIdAndProductoId(
                        usuarioId,
                        productoId
                );
    }

    public HistorialCarrito guardar(
            HistorialCarrito historialCarrito
    ) {
        return historialCarritoRepository.save(historialCarrito);
    }
}