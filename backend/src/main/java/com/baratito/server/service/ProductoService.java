package com.baratito.server.service;

import com.baratito.server.model.PrecioHistorico;
import com.baratito.server.model.ProductoSchema;
import com.baratito.server.persistence.interfaces.CacheRepository;
import com.baratito.server.persistence.interfaces.ProductoRepository;
import com.baratito.server.scraper.ScraperMaster;
import com.baratito.server.utils.CorrectorOrtografico;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


@Service
public class ProductoService {

    private final ScraperMaster scraperMaster;
    private final ProductoRepository productoRepository;
    private final CacheRepository cacheRepository;
    private final CorrectorOrtografico corrector;

    public ProductoService(ScraperMaster scraperMaster,
                           ProductoRepository productoRepository,
                           CacheRepository cacheRepository,
                           CorrectorOrtografico corrector) {
        this.scraperMaster = scraperMaster;
        this.productoRepository = productoRepository;
        this.cacheRepository = cacheRepository;
        this.corrector = corrector;
    }

    // ──────────────────────────── Búsqueda ────────────────────────────────────

    /**
     * Busca un producto en todos los supermercados y devuelve los resultados ordenados de menor a mayor precio.
     * @param query término de búsqueda (ej: "leche entera")
     * @return lista de productos de todos los supermercados, ordenada por precio
     */
    public List<ProductoSchema> buscarProductos(String query) {
        // Normalizar y corregir ortografía antes de cualquier otra cosa
        String queryCorregida = corrector.corregir(query.trim().toLowerCase());

        LocalDate hoy = LocalDate.now();

        if (cacheRepository.existeBusqueda(queryCorregida, hoy)) {
            return productoRepository.encontrarProductos(queryCorregida);
        }

        // Lógica de caché:
        // - Si esta query exacta ya fue buscada hoy → devuelve desde la BD (sin scrapear)
        // - Si no → scrapea, guarda en BD, registra la query en el caché
        List<ProductoSchema> resultados = scraperMaster.buscarEnTodos(queryCorregida);

        cacheRepository.registrarBusqueda(queryCorregida, hoy);

        return productoRepository.saveAllYObtenerOrdenados(resultados, queryCorregida);
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

    public List<String> obtenerSugerencias(String query) {
        if (query == null || query.trim().length() < 2) return List.of(); //evita hacer consultas si el texto es menor a 2 caracteres
        return productoRepository.obtenerSugerencias(query.trim().toLowerCase());
    }

    // ──────────────────────── Historial de precios ────────────────────────────

    /**
     * Devuelve los snapshots de precio de un producto en los últimos X días.
     * @param link identificador único del producto (su URL en el supermercado)
     * @param dias cantidad de días hacia atrás a consultar (ej: 7, 15, 30)
     * @return lista de snapshots ordenados por fecha ascendente
     */
    public List<PrecioHistorico> obtenerHistorialPrecios(String link, int dias) {
        if (dias <= 0 || dias > 90) dias = 30; // límite de seguridad
        return productoRepository.obtenerHistorial(link, dias);
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

    // TODO: alertas de precio
    // public void crearAlertaPrecio(String productoId, double precioObjetivo) { ... }

    // TODO: favoritos
    // public List<ProductoSchema> getFavoritos(Long usuarioId) { ... }
}