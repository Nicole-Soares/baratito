package com.baratito.server.persistence.impl;

import com.baratito.server.model.BusquedaCache;
import com.baratito.server.persistence.interfaces.CacheRepository;
import com.baratito.server.persistence.sql.BusquedaCacheSQLDAO;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public class CacheRepositoryImpl implements CacheRepository {

    private final BusquedaCacheSQLDAO busquedaCacheDAO;

    public CacheRepositoryImpl(BusquedaCacheSQLDAO busquedaCacheDAO) {
        this.busquedaCacheDAO = busquedaCacheDAO;
    }

    @Override
    public boolean existeBusqueda(String query, LocalDate fecha) {
        return busquedaCacheDAO.existsByQueryIgnoreCaseAndFecha(query, fecha);
    }

    @Override
    public void registrarBusqueda(String query, LocalDate fecha) {
        busquedaCacheDAO.save(new BusquedaCache(query, fecha));
    }

    @Override
    public void deleteAll() {
        busquedaCacheDAO.deleteAll();
    }
}