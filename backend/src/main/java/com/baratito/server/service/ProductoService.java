package com.baratito.server.service;

import com.baratito.server.model.ProductoSchema;
import com.baratito.server.scraper.ScraperMaster;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class ProductoService {

    private final ScraperMaster scraperMaster;

    // TODO: ProductoRepository cuando se implemente la base de datos
    // private final ProductoRepository productoRepository;

    public ProductoService(ScraperMaster scraperMaster) {
        this.scraperMaster = scraperMaster;
    }

    // ──────────────────────────── Búsqueda ────────────────────────────────────

    /**
     * Busca un producto en todos los supermercados y devuelve los resultados ordenados de menor a mayor precio.
     * @param query término de búsqueda (ej: "leche entera")
     * @return lista de productos de todos los supermercados, ordenada por precio
     */
    public List<ProductoSchema> buscarProductos(String query) {

        // TODO: cuando haya BD, verificar caché antes de scrapear
        // Optional<List<ProductoSchema>> cached = productoRepository.findFreshByQuery(query);
        // if (cached.isPresent()) return cached.get();

        List<ProductoSchema> resultados = scraperMaster.buscarEnTodos(query);

        // Filtrar y ordenar
        List<ProductoSchema> procesados = resultados.stream()
                .filter(p -> p.precio() > 0)
                .sorted(Comparator.comparingDouble(ProductoSchema::precio))
                .toList();

        // TODO: guardar resultados en BD para caché
        // productoRepository.saveAll(procesados, query);

        return procesados;
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
                .filter(p -> p.precio() > 0)
                .sorted(Comparator.comparingDouble(ProductoSchema::precio))
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