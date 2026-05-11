package com.baratito.server.model;

/**
 * Representa un producto normalizado devuelto por un scraper.
 */
public record ProductoSchema(
        String id,
        String source,       // "carrefour", "coto", etc.
        String nombre,
        String link,
        String imagen,
        boolean noDisponible,
        double precio,
        double precioLista   // precio original sin descuento
) {}