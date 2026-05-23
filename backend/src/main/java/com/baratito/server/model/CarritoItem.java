package com.baratito.server.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarritoItem {
    private Long id;
    private String nombre;
    private String source;
    private double precio;
    private String imagen;
    private int cantidad;

    public CarritoItem(Long id, String nombre, String source, double precio, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.source = source;
        this.precio = precio;
        this.imagen = imagen;
        this.cantidad = 1;
    }
}
