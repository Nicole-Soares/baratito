package com.baratito.server.scraper.impl;

import com.baratito.server.model.ProductoSchema;
import com.baratito.server.scraper.Scraper;
import com.baratito.server.scraper.core.ApiCore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Coto tiene su propia API (no VTEX), con una estructura de respuesta anidada
 * y descuentos embebidos como JSON dentro de un string.
 */
@Component
public class CotoScraper implements Scraper {

    private static final String BASE_URL = "https://www.cotodigital3.com.ar";
    private static final String SOURCE   = "coto";

    private final ApiCore apiCore;
    private final ObjectMapper objectMapper;

    public CotoScraper(ApiCore apiCore) {
        this.apiCore = apiCore;
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    // ────────────────────── DTOs de respuesta de Coto ─────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CotoResponseStructure(
            @JsonProperty("contents") List<CotoContenidoPrincipal> contents
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CotoContenidoPrincipal(
            @JsonProperty("Main") List<CotoSeccionMain> main
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CotoSeccionMain(
            @JsonProperty("contents") List<CotoContenidoInterno> contents
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CotoContenidoInterno(
            @JsonProperty("records") List<CotoProductoRespuesta> records
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CotoProductoRespuesta(
            @JsonProperty("detailsAction") CotoDetailsAction detailsAction,
            @JsonProperty("attributes")    CotoAtributosProducto attributes,
            @JsonProperty("records")       List<CotoRegistroSku> records
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CotoDetailsAction(
            @JsonProperty("recordState") String recordState
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CotoAtributosProducto(
            @JsonProperty("product.displayName")   List<String> productDisplayName,
            @JsonProperty("product.repositoryId")  List<String> productRepositoryId
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CotoRegistroSku(
            @JsonProperty("attributes") CotoAtributosSku attributes
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CotoAtributosSku(
            @JsonProperty("sku.referencePrice")    List<String> skuReferencePrice,
            @JsonProperty("sku.activePrice")       List<String> skuActivePrice,
            @JsonProperty("product.CONTENIDO")     List<String> productContenido,
            @JsonProperty("product.mediumImage.url") List<String> productMediumImageUrl,
            @JsonProperty("sku.quantity")          List<String> skuQuantity,
            @JsonProperty("product.dtoDescuentos") List<String> productDescuentos
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CotoDescuento(
            @JsonProperty("precioDescuento") String precioDescuento
    ) {}

    public record CotoProductoCrudo(
            CotoProductoRespuesta producto,
            List<CotoDescuento> descuentos
    ) {}

    // ──────────────────────── Implementación Scraper ──────────────────────────

    /**
     * Identificador único del scraper, usado para logging y respuestas.
     * @return "coto"
     */
    @Override
    public String getNombre() {
        return SOURCE;
    }

    /**
     * Busca productos en Coto usando su endpoint de búsqueda, normaliza la respuesta
     * @param query término de búsqueda (ej: "leche entera")
     * @return lista de productos encontrados, vacía si no hay resultados o hay error
     */
    @Override
    public List<ProductoSchema> buscar(String query) {
        return apiCore.buscar(new ApiCore.CoreProps<>(
                query,
                BASE_URL,
                SOURCE,
                q -> "/sitios/cdigi/categoria?Ntt=" + q + "&format=json",
                this::normalizar,
                this::extraer,
                CotoResponseStructure.class
        ));
    }

    /**
     * Normaliza la respuesta cruda de Coto extrayendo los productos y sus descuentos.
     * @param response respuesta cruda del endpoint de búsqueda de Coto
     * @return lista de productos crudos con su información y descuentos extraídos
     */
    private List<CotoProductoCrudo> normalizar(CotoResponseStructure response) {
        List<CotoProductoCrudo> resultado = new ArrayList<>();

        try {
            // Navegar la estructura anidada
            List<CotoProductoRespuesta> registros = response
                    .contents().get(0)
                    .main().get(1)          // índice 1: sección de resultados
                    .contents().get(0)
                    .records();

            for (CotoProductoRespuesta producto : registros) {
                if (producto.records() == null || producto.records().isEmpty()) continue;

                List<String> descuentosRaw = producto.records().get(0)
                        .attributes()
                        .productDescuentos();

                // Se saltea si no hay descuentos
                if (descuentosRaw == null || descuentosRaw.isEmpty()) continue;

                List<CotoDescuento> descuentos = parsearDescuentos(descuentosRaw.get(0));

                resultado.add(new CotoProductoCrudo(producto, descuentos));
            }

        } catch (IndexOutOfBoundsException e) {
        }

        return resultado;
    }

    /**
     * Extrae los campos de un producto crudo y lo transforma en ProductoSchema.
     * si hay descuento activo (precioDescuento > 0), se usa ese precio;
     * si no, se usa el precio de lista (skuActivePrice).
     * @param crudo producto crudo con su lista de descuentos
     * @return producto normalizado listo para ser mostrado al usuario
     */
    private ProductoSchema extraer(CotoProductoCrudo crudo) {
        CotoAtributosSku attrs = crudo.producto().records().get(0).attributes();

        double precioLista = parsearDouble(attrs.skuActivePrice().get(0));
        double precio = precioLista;

        if (!crudo.descuentos().isEmpty()) {
            double precioDescuento = parsearDouble(crudo.descuentos().get(0).precioDescuento());
            if (precioDescuento > 0) {
                precio = precioDescuento;
            }
        }

        // Limpiar el link quitando el parámetro ?format=json
        String link = crudo.producto().detailsAction().recordState()
                .replace("?format=json", "");

        return new ProductoSchema(
                crudo.producto().attributes().productRepositoryId().get(0),
                SOURCE,
                crudo.producto().attributes().productDisplayName().get(0),
                BASE_URL + link,
                attrs.productMediumImageUrl().get(0),
                "0".equals(attrs.skuQuantity().get(0)),
                precio,
                precioLista
        );
    }

    /**
     * Parsea el JSON de descuentos embebido como string dentro del array de atributos.
     * Ej: [{"precioDescuento":"1190.00"}, ...]
     * @param json string JSON a parsear
     * @return lista de descuentos, o vacía si no se pudo parsear o no hay descuentos
     */
    private List<CotoDescuento> parsearDescuentos(String json) {
        try {
            return objectMapper.readValue(
                    json,
                    objectMapper.getTypeFactory()
                            .constructCollectionType(List.class, CotoDescuento.class)
            );
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Parsea un string a double, devolviendo 0.0 en caso de formato inválido.
     * @param valor string a parsear
     * @return valor numérico, o 0.0 si no se pudo parsear
     */
    private double parsearDouble(String valor) {
        try {
            return Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}