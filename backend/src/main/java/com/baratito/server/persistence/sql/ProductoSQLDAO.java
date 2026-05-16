package com.baratito.server.persistence.sql;

import com.baratito.server.model.ProductoSchema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ProductoSQLDAO extends JpaRepository<ProductoSchema, Long> {

    List<ProductoSchema> findByNombreContainingIgnoreCaseOrderByPrecioAsc(String nombre);


    Optional<ProductoSchema> findByLink(String link);

    @Modifying
    @Transactional
    @Query("UPDATE ProductoSchema p SET p.disponibilidad = false " +
            "WHERE p.source = :source AND LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%'))")
    void resetearDisponibilidad(@Param("source") String source, @Param("query") String query);
}
