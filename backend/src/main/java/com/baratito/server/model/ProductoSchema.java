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
    Long id;

    @Column(name = "source")
    String source;       // "carrefour", "coto", etc.

    @Column(name = "nombre", nullable = false)
    String nombre;

    @Column(name = "link", length = 1024) // para que no tire error, la bd por default tiene 255 char, con eso soporta mas
    String link;

    @Column(name = "imagen")
    String imagen;

    @Column(name = "disponibilidad")
    boolean disponibilidad;

    @Column(name = "precio", columnDefinition = "DOUBLE PRECISION CHECK (precio >= 0)")
    double precio;

    @Column(name = "precioLista", columnDefinition = "DOUBLE PRECISION CHECK (precioLista >= 0)")
    double precioLista;   // precio original sin descuento

    @Column(name = "actualizado")
    LocalDate actualizado;

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