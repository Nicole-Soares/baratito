package com.baratito.server.scraper.core;

import com.baratito.server.model.ProductoSchema;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.function.Function;

/**
 * Core genérico para scrapers basados en APIs REST.
 * Maneja el ciclo completo: request HTTP → normalización → extracción → ProductoSchema.
 */
@Component
public class ApiCore {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ApiCore(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Props genéricas necesarias para ejecutar el ciclo completo de scraping.
     * @param <TResponse> tipo de la respuesta cruda del endpoint
     * @param <TRaw>      tipo del producto "crudo" antes de normalizar a ProductoSchema
     */
    public record CoreProps<TResponse, TRaw>(
            String query,
            String baseUrl,
            String source,

            // Construye la URL de búsqueda a partir de la query
            Function<String, String> searchPattern,

            // Transforma la respuesta cruda del endpoint en una lista de productos raw
            Function<TResponse, List<TRaw>> normalizer,

            // Extrae los campos necesarios de cada producto raw → ProductoSchema
            Function<TRaw, ProductoSchema> extractor,

            // Clase del tipo de respuesta, necesaria para deserializar con Jackson
            Class<TResponse> responseType
    ) {}

    /**
     * Ejecuta el ciclo completo de scraping para una query dada.
     * @param <TResponse> tipo de la respuesta cruda del endpoint
     * @param <TRaw>      tipo del producto "crudo" antes de normalizar a ProductoSchema
     * @return lista de productos normalizados, vacía en caso de error
     */
    public <TResponse, TRaw> List<ProductoSchema> buscar(CoreProps<TResponse, TRaw> props) {
        String url = props.baseUrl() + props.searchPattern().apply(props.query());

        try {
            HttpHeaders headers = buildHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<TResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    props.responseType()
            );

            if (response.getBody() == null) {
                return List.of();
            }

            List<TRaw> rawProducts = props.normalizer().apply(response.getBody());

            List<ProductoSchema> productos = rawProducts.stream()
                    .map(raw -> {
                        try {
                            return props.extractor().apply(raw);
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(p -> p != null)
                    .toList();

            return productos;

        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Headers necesarios para que los supermercados no bloqueen el request.
     * Sin User-Agent de browser, la mayoría rechaza la conexión.
     * @return HttpHeaders con User-Agent, Accept y Accept-Language configurados
     */
    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/120.0.0.0 Safari/537.36");
        headers.set("Accept", "application/json, text/plain, */*");
        headers.set("Accept-Language", "es-AR,es;q=0.9");
        return headers;
    }
}