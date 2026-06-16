package com.baratito.server.persistence.impl;

import com.baratito.server.model.CarritoItem;
import com.baratito.server.persistence.interfaces.CarritoRepository;
import com.baratito.server.persistence.sql.CarritoSQLDAO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CarritoRepositoryImpl implements CarritoRepository {

    private final CarritoSQLDAO carritoSQLDAO;


    public CarritoRepositoryImpl(CarritoSQLDAO carritoSQLDAO) {
        this.carritoSQLDAO = carritoSQLDAO;
    }


    @Override
    public Optional<CarritoItem> findById(Long productoId) {
        return carritoSQLDAO.findByProductoId(productoId);
    }


    @Override
    public List<CarritoItem> findAll() {
        return carritoSQLDAO.findAll();
    }

    @Override
    public void save(CarritoItem item) {
        carritoSQLDAO.save(item);
    }

    @Override
    public void delete(CarritoItem item) {
        carritoSQLDAO.delete(item);
    }

    @Override
    public void deleteAll() {
        carritoSQLDAO.deleteAll();
    }

    @Override
    public List<CarritoItem> findByUsuarioId(Long usuarioId) {
        return carritoSQLDAO.findByUsuario_Id(usuarioId);
    }

    @Override
    public Optional<CarritoItem> findByUsuarioIdAndProductoId(Long usuarioId, Long productoId) {
        return carritoSQLDAO.findByUsuario_IdAndProductoId(usuarioId, productoId);
    }

    @Override
    public Optional<CarritoItem> findByProductoId(Long productoId) {
        return carritoSQLDAO.findByProductoId(productoId);
    }

}
