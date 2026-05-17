package com.baratito.server.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Registra qué queries se buscaron y en qué fecha.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "busqueda_cache")
public class BusquedaCache {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "query", nullable = false)
    private String query;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    public BusquedaCache(String query, LocalDate fecha) {
        this.query = query;
        this.fecha = fecha;
    }
}