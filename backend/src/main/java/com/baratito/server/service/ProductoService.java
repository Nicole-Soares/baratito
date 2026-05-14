package com.baratito.server.service;

import com.baratito.server.model.ProductoSchema;
import com.baratito.server.persistence.interfaces.ProductoRepository;
import com.baratito.server.scraper.ScraperMaster;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Service
public class ProductoService {

    private final ScraperMaster scraperMaster;
    private final ProductoRepository productoRepository;

    public ProductoService(ScraperMaster scraperMaster, ProductoRepository productoRepository) {

        this.scraperMaster = scraperMaster;
        this.productoRepository = productoRepository;

    }

    // ──────────────────────────── Búsqueda ────────────────────────────────────

    /**
     * Busca un producto en todos los supermercados y devuelve los resultados ordenados de menor a mayor precio.
     * @param query término de búsqueda (ej: "leche entera")
     * @return lista de productos de todos los supermercados, ordenada por precio
     */
    public List<ProductoSchema> buscarProductos(String query) {


        List<ProductoSchema> productosEnBase = productoRepository.encontrarProductos(query);

        //chequeo si se encuentra en base de datos y / o estan desactualizados

        if (productosEnBase.isEmpty() || !estanActualizados (productosEnBase)) {

            //busco en el scraper
            List<ProductoSchema> resultados = scraperMaster.buscarEnTodos(query);


            // guardar en base y retornar de manera ordenada
            //query es el tipo de producto
            return productoRepository.saveAllYObtenerOrdenados(resultados, query);

        } else {

            return productosEnBase;

        }

    }

    private boolean estanActualizados(List<ProductoSchema> productosEnBase) {
        if (productosEnBase.isEmpty()) {
            return false;
        }

        LocalDate hoy = LocalDate.now();

        // Esto verifica si todos los productos son de hoy
        // (anyMatch si te basta con que uno solo lo sea)
        return productosEnBase.stream()
                .allMatch(p -> hoy.equals(p.getActualizado()));
    }

    /**
     * Busca productos y los devuelve agrupados por supermercado.
     * Útil para la vista de comparación lado a lado
     * @return mapa { "carrefour": [...], "coto": [...] }
     */
    public Map<String, List<ProductoSchema>> buscarProductosAgrupados(String query) {
        return scraperMaster.buscarAgrupado(query);
    }

    /**
     * Busca productos solo en un supermercado específico.
     * @param query   término de búsqueda
     * @param fuente  nombre del supermercado ("carrefour", "coto", etc.)
     */
    public List<ProductoSchema> buscarEnSupermercado(String query, String fuente) {

        return scraperMaster.buscarEnUno(query, fuente)
                .stream()
                .filter(p -> p.getPrecio() > 0)
                .sorted(Comparator.comparingDouble(ProductoSchema::getPrecio))
                .toList();

    }

    // ──────────────────────── Consultas de metadatos ──────────────────────────

    /**
     * Devuelve los supermercados disponibles.
     * @return lista de nombres de supermercados (ej: ["carrefour", "coto", "dia"])
     */
    public List<String> getSupermercadosDisponibles() {
        return scraperMaster.getSupermercadosDisponibles();
    }

    // ───────────────────── Futuras operaciones con BD ─────────────────────────

    // TODO: historial de precios
    // public List<PrecioHistorico> getHistorialPrecios(String productoId) { ... }

    // TODO: alertas de precio
    // public void crearAlertaPrecio(String productoId, double precioObjetivo) { ... }

    // TODO: favoritos
    // public List<ProductoSchema> getFavoritos(Long usuarioId) { ... }
}