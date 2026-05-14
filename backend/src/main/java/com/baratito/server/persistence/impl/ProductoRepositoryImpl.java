package com.baratito.server.persistence.impl;

import com.baratito.server.model.ProductoSchema;
import com.baratito.server.persistence.interfaces.ProductoRepository;
import com.baratito.server.persistence.sql.ProductoSQLDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class ProductoRepositoryImpl implements ProductoRepository {

    private final ProductoSQLDAO productoSQLDAO;


public ProductoRepositoryImpl(ProductoSQLDAO productoSQLDAO) {
    this.productoSQLDAO = productoSQLDAO;
}


    @Override
    public List<ProductoSchema> encontrarProductos(String query) {
        // busca por nombre del producto, tanto individualmente o si es contenido, ignora si fue escrito en mayus o minus,
        //ordena de manera ascendente
        return productoSQLDAO.findByNombreContainingIgnoreCaseOrderByPrecioAsc(query);

    }
    /*
    @Override
    public List<ProductoSchema> saveAllYObtenerOrdenados(List<ProductoSchema> resultados, String query) {

        // se guarda
        productoSQLDAO.saveAll(resultados);

        //se usa la query para traer los productos ordenados de menor a mayor
        return  productoSQLDAO.findByNombreContainingIgnoreCaseOrderByPrecioAsc(query);

    }
*/


    @Override
    @Transactional
    public List<ProductoSchema> saveAllYObtenerOrdenados(List<ProductoSchema> resultados, String query) {

        if (resultados.isEmpty()) return List.of();

        LocalDate hoy = LocalDate.now();

        // 1. Identificamos qué supermercados vinieron en el scraper (Coto, Carrefour, etc.)
        Set<String> sourcesInvolucrados = resultados.stream()
                .map(ProductoSchema::getSource)
                .collect(Collectors.toSet());

        // 2. buscamos los productos especificos de los super especificos
        for (String source : sourcesInvolucrados) {
            productoSQLDAO.resetearDisponibilidad(source, query);
        }

        // 3. Procesamos la lista (que puede traer 50 leches de distintas marcas de Coto)
        for (ProductoSchema p : resultados) {

            // Criterio 3: Integridad
            if (p.getPrecio() <= 0 || p.getNombre() == null || p.getNombre().isBlank()) {
                continue;
            }

            // Buscamos por el link cada producto (ID único del producto en la web)
            productoSQLDAO.findByLink(p.getLink()).ifPresentOrElse(
                    existente -> {
                        // Si existe: Actualizamos
                        existente.setPrecio(p.getPrecio());
                        existente.setPrecioLista(p.getPrecioLista());
                        existente.setDisponibilidad(true);
                        existente.setActualizado(hoy);
                        productoSQLDAO.save(existente);
                    },
                    () -> {
                        // Si es nuevo: Lo creamos con disponibilidad true
                        p.setDisponibilidad(true);
                        p.setActualizado(hoy);
                        productoSQLDAO.save(p);
                    }
            );
        }

        // se devuelve todo ordenado
        return productoSQLDAO.findByNombreContainingIgnoreCaseOrderByPrecioAsc(query);
    }

    @Override
    public List<ProductoSchema> findAll() {
        return productoSQLDAO.findAll();
    }

    @Override
    public void deleteAll() {
        productoSQLDAO.deleteAll();
    }
}
