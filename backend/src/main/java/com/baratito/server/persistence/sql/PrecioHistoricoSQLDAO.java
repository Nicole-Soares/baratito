package com.baratito.server.persistence.sql;

import com.baratito.server.model.PrecioHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PrecioHistoricoSQLDAO extends JpaRepository<PrecioHistorico, Long> {

    /**
     * Trae todos los snapshots de un producto en los últimos X días.
     * Se usa el link como identificador único del producto.
     * @param link el link del producto (ej: "https://www.coto.com.ar/leche-entera-1l")
     * @param desde fecha desde la cual traer los snapshots (ej: hoy.minusDays(
     * @return lista de snapshots ordenada por fecha ascendente (del más antiguo al más reciente)
     */
    @Query("SELECT p FROM PrecioHistorico p " +
            "WHERE p.productoLink = :link " +
            "AND p.fecha >= :desde " +
            "ORDER BY p.fecha ASC")
    List<PrecioHistorico> findByProductoLinkAndFechaGreaterThanEqual(
            @Param("link") String link,
            @Param("desde") LocalDate desde
    );

    /**
     * Verifica si ya existe un snapshot para este producto en esta fecha.
     * Evita duplicados si se busca el mismo producto más de una vez en el día.
     * @param productoLink el link del producto
     * @param fecha la fecha del snapshot (ej: hoy)
     * @return true si ya existe un snapshot para este producto en esta fecha, false si
     */
    boolean existsByProductoLinkAndFecha(String productoLink, LocalDate fecha);
}