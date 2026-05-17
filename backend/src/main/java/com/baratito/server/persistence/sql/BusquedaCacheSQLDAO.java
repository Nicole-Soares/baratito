package com.baratito.server.persistence.sql;

import com.baratito.server.model.BusquedaCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface BusquedaCacheSQLDAO extends JpaRepository<BusquedaCache, Long> {

    boolean existsByQueryIgnoreCaseAndFecha(String query, LocalDate fecha);
}