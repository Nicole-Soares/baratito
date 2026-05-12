package com.baratito.server.scraper.core;

import com.baratito.server.model.ProductoSchema;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Core para supermercados que usan la plataforma VTEX.
 * VTEX tiene una Search API pública que devuelve JSON con una estructura relativamente estándar,
 */
@Component
public class VtexCore {

    private final RestTemplate restTemplate;

    public VtexCore(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public record VtexCoreProps(String query, String baseUrl, String source) {}

    // ──────────────────────── DTOs de respuesta VTEX ──────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VtexProducto(
            @JsonProperty("productId")   String productId,
            @JsonProperty("productName") String productName,
            @JsonProperty("linkText")    String linkText,
            @JsonProperty("items")       List<VtexItem> items
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VtexItem(
            @JsonProperty("itemId")  String itemId,
            @JsonProperty("images")  List<VtexImagen> images,
            @JsonProperty("sellers") List<VtexVendedor> sellers
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VtexImagen(
            @JsonProperty("imageUrl") String imageUrl
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VtexVendedor(
            @JsonProperty("commertialOffer") VtexOferta commertialOffer
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VtexOferta(
            @JsonProperty("Price")             double price,
            @JsonProperty("ListPrice")         double listPrice,
            @JsonProperty("AvailableQuantity") int availableQuantity
    ) {}

    // ─────────────────────── Métod0 principal ─────────────────────────────────

    /**
     * Busca productos en un supermercado VTEX usando la Search API estándar.
     * @param props props genéricas necesarias para ejecutar el ciclo completo de scraping
     * @return lista de productos normalizados, vacía en caso de error o sin resultados
     */
    public List<ProductoSchema> buscar(VtexCoreProps props) {

        String url = props.baseUrl()
                + "/api/catalog_system/pub/products/search/"
                + props.query()
                + "?_from=0&_to=49";

        try {
            HttpHeaders headers = buildHeaders(props.baseUrl());
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<VtexProducto[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    VtexProducto[].class
            );

            if (response.getBody() == null) return List.of();

            List<ProductoSchema> productos = Arrays.stream(response.getBody())
                    .map(p -> mapearProducto(p, props))
                    .filter(Objects::nonNull)
                    .toList();

            return productos;

        } catch (Exception e) {
            return List.of();
        }
    }

    private ProductoSchema mapearProducto(VtexProducto producto, VtexCoreProps props) {
        try {
            if (producto.items() == null || producto.items().isEmpty()) return null;

            VtexItem item = producto.items().get(0);

            if (item.sellers() == null || item.sellers().isEmpty()) return null;

            VtexOferta oferta = item.sellers().get(0).commertialOffer();
            String imagen = (item.images() != null && !item.images().isEmpty())
                    ? item.images().get(0).imageUrl()
                    : "";

            return new ProductoSchema(
                    producto.productId(),
                    props.source(),
                    producto.productName(),
                    props.baseUrl() + "/" + producto.linkText() + "/p",
                    imagen,
                    oferta.availableQuantity() == 0,
                    oferta.price(),
                    oferta.listPrice()
            );
        } catch (Exception e) {
            return null;
        }
    }

    private HttpHeaders buildHeaders(String baseUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/120.0.0.0 Safari/537.36");
        headers.set("Accept", "application/json");
        headers.set("Referer", baseUrl);
        return headers;
    }
}