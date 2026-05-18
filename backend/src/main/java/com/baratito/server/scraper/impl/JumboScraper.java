package com.baratito.server.scraper.impl;

import com.baratito.server.model.ProductoSchema;
import com.baratito.server.scraper.Scraper;
import com.baratito.server.scraper.core.ApiCore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class JumboScraper implements Scraper {

    private static final String BASE_URL = "https://www.jumbo.com.ar";
    private static final String SOURCE   = "jumbo";

    private final ApiCore apiCore;
    private final ObjectMapper objectMapper;

    public JumboScraper(ApiCore apiCore) {
        this.apiCore = apiCore;
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    // ───────────────────── DTOs de respuesta de Jumbo ─────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record JumboProductoRespuesta(
            @JsonProperty("productId")   String productId,
            @JsonProperty("productName") String productName,
            @JsonProperty("link")        String link,

            @JsonProperty("ProductData") List<String> productData,

            @JsonProperty("items") List<JumboItem> items
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record JumboItem(
            @JsonProperty("images")  List<JumboImagen> images,
            @JsonProperty("sellers") List<JumboVendedor> sellers
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record JumboImagen(
            @JsonProperty("imageUrl") String imageUrl
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record JumboVendedor(
            @JsonProperty("commertialOffer") JumboOferta commertialOffer
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record JumboOferta(
            @JsonProperty("Price")                float price,
            @JsonProperty("ListPrice")            float listPrice,
            @JsonProperty("PriceWithoutDiscount") float priceWithoutDiscount,
            @JsonProperty("AvailableQuantity")    int availableQuantity,
            @JsonProperty("IsAvailable")          boolean isAvailable
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record JumboProductData(
            @JsonProperty("MeasurementUnit") String measurementUnit,
            @JsonProperty("UnitMultiplier")  float unitMultiplier
    ) {}

    public record JumboProductoCrudo(
            JumboProductoRespuesta producto,
            JumboProductData productData
    ) {}

    // ─────────────────────── Implementación Scraper ───────────────────────────

    @Override
    public String getNombre() {
        return SOURCE;
    }

    @Override
    public List<ProductoSchema> buscar(String query) {
        return apiCore.buscar(new ApiCore.CoreProps<>(
                query,
                BASE_URL,
                SOURCE,
                // Jumbo usa /api/catalog_system con ?ft= en vez de path directo
                q -> "/api/catalog_system/pub/products/search/?ft=" + q,
                this::normalizar,
                this::extraer,
                JumboProductoRespuesta[].class
        ));
    }

    /**
     * Filtra productos válidos y parsea el ProductData embebido.
     * Descarta productos sin ProductData
     * @param response respuesta cruda del endpoint de Jumbo
     * @return lista de productos "crudos" listos para extraer a ProductoSchema
     */
    private List<JumboProductoCrudo> normalizar(JumboProductoRespuesta[] response) {
        List<JumboProductoCrudo> resultado = new ArrayList<>();

        for (JumboProductoRespuesta producto : response) {
            if (producto.productData() == null || producto.productData().isEmpty()) continue;
            if (producto.items() == null || producto.items().isEmpty()) continue;

            try {
                JumboProductData productData = objectMapper.readValue(
                        producto.productData().get(0),
                        JumboProductData.class
                );
                resultado.add(new JumboProductoCrudo(producto, productData));
            } catch (Exception ignored) {
            }
        }

        return resultado;
    }

    /**
     * Extrae los campos del producto crudo y construye el ProductoSchema.
     * @param crudo producto crudo con datos ya normalizados
     * @return producto normalizado listo para ser consumido por el Master
     */
    private ProductoSchema extraer(JumboProductoCrudo crudo) {
        JumboItem item = crudo.producto().items().get(0);
        JumboOferta oferta = item.sellers().get(0).commertialOffer();

        String imagen = (item.images() != null && !item.images().isEmpty())
                ? item.images().get(0).imageUrl()
                : "";

        return new ProductoSchema(
                SOURCE,
                crudo.producto().productName(),
                crudo.producto().link(),
                imagen,
                oferta.isAvailable(),
                oferta.price(),
                oferta.priceWithoutDiscount() > 0 ? oferta.priceWithoutDiscount() : oferta.price(),
                LocalDate.now()
        );
    }
}