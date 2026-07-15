package com.baratito.server.controller.dto.favorito;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavoritoDTO {

    private Long id;
    private String nombre;
    private String source;
    private String imagen;
    private double precio;
    private double precioLista;
    private String link;

    public FavoritoDTO(Long id, String nombre, String source, String imagen, double precio, double precioLista, String link) {
        this.id = id;
        this.nombre = nombre;
        this.source = source;
        this.imagen = imagen;
        this.precio = precio;
        this.precioLista = precioLista;
        this.link = link;
    }
}
