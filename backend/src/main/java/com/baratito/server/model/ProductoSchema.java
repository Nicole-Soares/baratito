package com.baratito.server.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;


/**
 * Representa un producto normalizado devuelto por un scraper.
 */

@Setter
@Getter
@NoArgsConstructor
@Entity(name = "ProductoSchema")
public class ProductoSchema {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "source")
    private String source;       // "carrefour", "coto", etc.

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "link", length = 1024) // para que no tire error, la bd por default tiene 255 char, con eso soporta mas
    private String link;

    @Column(name = "imagen")
    private String imagen;

    @Column(name = "disponibilidad")
    private boolean disponibilidad;

    @Column(name = "precio", columnDefinition = "DOUBLE PRECISION CHECK (precio >= 0)")
    private double precio;

    @Column(name = "precio_lista", columnDefinition = "DOUBLE PRECISION CHECK (precio_lista >= 0)")
    private double precioLista;   // precio original sin descuento

    @Column(name = "actualizado")
    private LocalDate actualizado;

    public ProductoSchema(String source, String nombre, String link, String imagen, boolean disponibilidad, double precio, double precioLista, LocalDate actualizado) {
        this.source = source;
        this.nombre = nombre;
        this.link = link;
        this.imagen = imagen;
        this.disponibilidad = disponibilidad;
        this.precio = precio;
        this.precioLista = precioLista;
        this.actualizado = actualizado;
    }

}