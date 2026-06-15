package com.baratito.server.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Snapshot del precio de un producto en una fecha determinada.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity(name = "PrecioHistorico")
@Table(name = "precio_historico")
public class PrecioHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "producto_link", length = 1024, nullable = false)
    private String productoLink;

    @Column(name = "source", nullable = false)
    private String source;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "precio", nullable = false)
    private double precio;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    public PrecioHistorico(String productoLink, String source, String nombre, double precio, LocalDate fecha) {
        this.productoLink = productoLink;
        this.source = source;
        this.nombre = nombre;
        this.precio = precio;
        this.fecha = fecha;
    }
}