package com.baratito.server.scraper;

import com.baratito.server.model.ProductoSchema;

import java.util.List;

/**
 * Contrato que debe implementar cada scraper de supermercado.
 * Cada scraper sabe cómo buscar productos en su propio supermercado.
 */
public interface Scraper {

    /**
     * Identificador único del supermercado (ej: "carrefour", "coto").
     * Se usa para logging, respuestas y agrupamiento de resultados.
     */
    String getNombre();

    /**
     * Busca productos que coincidan con la query en el supermercado correspondiente.
     * @param query término de búsqueda (ej: "leche entera")
     * @return lista de productos encontrados, vacía si no hay resultados o hay error
     */
    List<ProductoSchema> buscar(String query);
}