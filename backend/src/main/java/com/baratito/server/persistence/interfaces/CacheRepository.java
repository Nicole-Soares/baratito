package com.baratito.server.persistence.interfaces;

import java.time.LocalDate;

public interface CacheRepository {

    boolean existeBusqueda(String query, LocalDate fecha);
    void registrarBusqueda(String query, LocalDate fecha);
    void deleteAll();
}