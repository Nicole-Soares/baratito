package com.baratito.server.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "carrito_items")
@Getter
@Setter
public class CarritoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // este es el id del registro en la tabla

    @Column(name = "producto_id", nullable = false)
    private Long productoId; // este es el id del producto real

    private String nombre;
    private String source;
    private double precio;
    private String imagen;
    private int cantidad;

    public CarritoItem(Long productoId, String nombre, String source, double precio, String imagen) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.source = source;
        this.precio = precio;
        this.imagen = imagen;
        this.cantidad = 1;
    }

    public CarritoItem() {} // constructor vacío para JPA
}

