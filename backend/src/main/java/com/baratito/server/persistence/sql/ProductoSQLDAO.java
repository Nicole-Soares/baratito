package com.baratito.server.persistence.sql;

import com.baratito.server.model.ProductoSchema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProductoSQLDAO extends JpaRepository<ProductoSchema, Long> {

    List<ProductoSchema> findByNombreContainingIgnoreCaseOrderByPrecioAsc(String nombre);

    List<ProductoSchema> findByNombreContainingIgnoreCaseAndActualizadoAndDisponibilidadTrueOrderByPrecioAsc(String nombre, LocalDate actualizado);

    @Query(value = """
            SELECT *
            FROM producto_schema p
            WHERE p.disponibilidad = true
              AND (
                  LOWER(unaccent(p.nombre)) LIKE LOWER(CONCAT('%', unaccent(:query), '%'))
                  OR translate(LOWER(unaccent(p.nombre)), 'zcsvb', 'sssss')
                      LIKE LOWER(CONCAT('%', translate(unaccent(:query), 'zcsvb', 'sssss'), '%'))
                  OR similarity(
                      translate(LOWER(unaccent(p.nombre)), 'zcsvb', 'sssss'),
                      translate(unaccent(LOWER(:query)), 'zcsvb', 'sssss')
                  ) >= 0.25
              )
            ORDER BY p.precio ASC
            """, nativeQuery = true)
    List<ProductoSchema> buscarPorTextoDifuso(@Param("query") String query);

    Optional<ProductoSchema> findByLink(String link);

    @Modifying
    @Transactional
    @Query("UPDATE ProductoSchema p SET p.disponibilidad = false " +
            "WHERE p.source = :source AND LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%'))")
    void resetearDisponibilidad(@Param("source") String source, @Param("query") String query);
}
