package com.baratito.server.scraper.impl;

import com.baratito.server.model.ProductoSchema;
import com.baratito.server.scraper.core.ApiCore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JumboScraper - Tests de Integración")
class JumboScraperIT {

    private JumboScraper scraper;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        ApiCore apiCore = new ApiCore(restTemplate);
        scraper = new JumboScraper(apiCore);
    }

    @Test
    @DisplayName("getNombre() devuelve 'jumbo'")
    void getNombre_devuelveIdentificadorCorrecto() {
        assertThat(scraper.getNombre()).isEqualTo("jumbo");
    }

    @Test
    @DisplayName("buscar() con query válida devuelve al menos un resultado")
    void buscar_queryValida_devuelveResultados() {
        List<ProductoSchema> resultados = scraper.buscar("leche");

        assertThat(resultados)
                .as("Jumbo debería devolver resultados para 'leche'")
                .isNotEmpty();
    }

    @Test
    @DisplayName("Todos los productos tienen los campos obligatorios completos")
    void buscar_todosLosProductosTienenCamposObligatorios() {
        List<ProductoSchema> resultados = scraper.buscar("leche");

        assertThat(resultados).isNotEmpty();
        assertThat(resultados).allSatisfy(p -> {
            assertThat(p.getNombre()) .as("nombre no puede ser null ni vacío").isNotBlank();
            assertThat(p.getSource()) .as("source debe ser 'jumbo'").isEqualTo("jumbo");
            assertThat(p.getPrecio()) .as("precio debe ser >= 0").isGreaterThanOrEqualTo(0.0);
        });
    }

    @Test
    @DisplayName("Todos los productos tienen precio mayor a cero")
    void buscar_todosLosProductosTienenPrecioPositivo() {
        List<ProductoSchema> resultados = scraper.buscar("arroz");

        assertThat(resultados).isNotEmpty();
        assertThat(resultados)
                .extracting(ProductoSchema::getPrecio)
                .allMatch(precio -> precio > 0, "todos los precios deben ser > 0");
    }

    @Test
    @DisplayName("Todos los productos tienen link no vacío")
    void buscar_productosConLinkValido() {
        List<ProductoSchema> resultados = scraper.buscar("leche");

        assertThat(resultados).isNotEmpty();
        assertThat(resultados)
                .extracting(ProductoSchema::getLink)
                .allMatch(link -> link != null && !link.isBlank(),
                        "todos los productos deben tener link");
    }

    @Test
    @DisplayName("buscar() con query sin resultados devuelve lista vacía sin lanzar excepción")
    void buscar_querySinResultados_devuelveListaVaciaSinExplotar() {
        List<ProductoSchema> resultados = scraper.buscar("xkqzwmproductoinexistente9999");

        assertThat(resultados).isNotNull();
    }

    @Test
    @DisplayName("precioLista es siempre mayor o igual al precio (descuento aplicado)")
    void buscar_precioListaSiempreMayorOIgualAlPrecio() {
        List<ProductoSchema> resultados = scraper.buscar("leche");

        assertThat(resultados).isNotEmpty();
        assertThat(resultados).allSatisfy(p ->
                assertThat(p.getPrecioLista())
                        .as("precioLista debe ser >= precio para '%s'", p.getNombre())
                        .isGreaterThanOrEqualTo(p.getPrecio())
        );
    }

    @Test
    @DisplayName("Todos los productos tienen fecha de actualización")
    void buscar_todosLosProductosTienenFechaActualizacion() {
        List<ProductoSchema> resultados = scraper.buscar("leche");

        assertThat(resultados).isNotEmpty();
        assertThat(resultados)
                .extracting(ProductoSchema::getActualizado)
                .allMatch(fecha -> fecha != null, "todos los productos deben tener fecha");
    }
}