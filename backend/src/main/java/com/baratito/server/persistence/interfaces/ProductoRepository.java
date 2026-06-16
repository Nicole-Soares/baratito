package com.baratito.server.persistence.interfaces;

import com.baratito.server.model.PrecioHistorico;
import com.baratito.server.model.ProductoSchema;

import java.util.List;

public interface ProductoRepository {

    List<ProductoSchema> encontrarProductos(String query);
    List<ProductoSchema> saveAllYObtenerOrdenados(List<ProductoSchema> resultados, String query);
    List<PrecioHistorico> obtenerHistorial(String link, int dias);
    List<ProductoSchema> findAll();
    void deleteAll();
    List<String> obtenerSugerencias(String query);
}
