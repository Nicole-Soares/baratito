package com.baratito.server.controller.dto.producto;

import com.baratito.server.model.ProductoSchema;

import java.time.LocalDate;

public class ProductoDTO {

    private Long id;
    private String source;
    private String nombre;
    private String link;
    private String imagen;
    private boolean disponibilidad;
    private double precio;
    private double precioLista;
    private LocalDate actualizado;

    public ProductoDTO(Long id, String source, String nombre, String link, String imagen,
                       boolean disponibilidad, double precio, double precioLista, LocalDate actualizado) {
        this.id = id;
        this.source = source;
        this.nombre = nombre;
        this.link = link;
        this.imagen = imagen;
        this.disponibilidad = disponibilidad;
        this.precio = precio;
        this.precioLista = precioLista;
        this.actualizado = actualizado;
    }

    public ProductoDTO(ProductoSchema p) {
        this(p.getId(), p.getSource(), p.getNombre(), p.getLink(), p.getImagen(),
                p.isDisponibilidad(), p.getPrecio(), p.getPrecioLista(), p.getActualizado());
    }

    public Long getId() { return id; }
    public String getSource() { return source; }
    public String getNombre() { return nombre; }
    public String getLink() { return link; }
    public String getImagen() { return imagen; }
    public boolean isDisponibilidad() { return disponibilidad; }
    public double getPrecio() { return precio; }
    public double getPrecioLista() { return precioLista; }
    public LocalDate getActualizado() { return actualizado; }
}