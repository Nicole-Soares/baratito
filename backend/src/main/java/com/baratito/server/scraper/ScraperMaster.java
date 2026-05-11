package com.baratito.server.scraper;

import com.baratito.server.model.ProductoSchema;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Para agregar un nuevo supermercado/scraper tiene que
 * implementar {@link Scraper} y anotarla con @Component.
 */
@Component
public class ScraperMaster {

    private final List<Scraper> scrapers;
    private final ExecutorService executor;

    /**
     * Spring inyecta automáticamente todos los beans que implementen Scraper.
     * NO hace falta modificar este constructor cuando se agrega un nuevo scraper.
     */
    public ScraperMaster(List<Scraper> scrapers) {
        this.scrapers = scrapers;
        // Un thread por scraper para ejecutarlos en paralelo
        this.executor = Executors.newFixedThreadPool(Math.max(scrapers.size(), 1));
    }

    /**
     * Busca un producto en TODOS los supermercados en paralelo.
     * @param query término de búsqueda
     * @return lista con todos los resultados de todos los supermercados, ya normalizados
     */
    public List<ProductoSchema> buscarEnTodos(String query) {

        // Lanzar todos los scrapers en paralelo
        List<CompletableFuture<List<ProductoSchema>>> futures = scrapers.stream()
                .map(scraper -> CompletableFuture
                        .supplyAsync(() -> buscarConManejoDErrores(scraper, query), executor)
                )
                .toList();

        // Esperar a que todos terminen y unificar resultados
        List<ProductoSchema> todos = futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .toList();

        return todos;
    }

    /**
     * Busca en todos los supermercados y devuelve los resultados agrupados por supermercado.
     * @param query término de búsqueda
     * @return mapa { "carrefour": [...], "coto": [...] }
     */
    public Map<String, List<ProductoSchema>> buscarAgrupado(String query) {
        return buscarEnTodos(query).stream()
                .collect(Collectors.groupingBy(ProductoSchema::source));
    }

    /**
     * Busca solo en un supermercado específico.
     * @param query término de búsqueda
     * @param fuente  nombre del supermercado (ej: "coto", "carrefour")
     * @return lista de productos, vacía si el supermercado no existe
     */
    public List<ProductoSchema> buscarEnUno(String query, String fuente) {
        return scrapers.stream()
                .filter(s -> s.getNombre().equalsIgnoreCase(fuente))
                .findFirst()
                .map(s -> buscarConManejoDErrores(s, query))
                .orElseGet(List::of);
    }

    /**
     * Devuelve los nombres de los supermercados disponibles.
     * @return lista de nombres de supermercados (ej: ["carrefour", "coto"])
     */
    public List<String> getSupermercadosDisponibles() {
        return scrapers.stream()
                .map(Scraper::getNombre)
                .toList();
    }

    /**
     * Ejecuta un scraper individual con manejo de errores aislado.
     * Si un scraper falla, no afecta a los demás.
     */
    private List<ProductoSchema> buscarConManejoDErrores(Scraper scraper, String query) {
        try {
            return scraper.buscar(query);
        } catch (Exception e) {
            return List.of();
        }
    }
}