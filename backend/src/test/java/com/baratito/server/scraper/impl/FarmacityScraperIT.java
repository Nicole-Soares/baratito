package com.baratito.server.scraper.impl;

import com.baratito.server.model.ProductoSchema;
import com.baratito.server.scraper.core.VtexCore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FarmacityScraper - Tests de Integración")
class FarmacityScraperIT {

    private FarmacityScraper scraper;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        VtexCore vtexCore = new VtexCore(restTemplate);
        scraper = new FarmacityScraper(vtexCore);
    }

    @Test
    @DisplayName("getNombre() devuelve 'farmacity'")
    void getNombre_devuelveIdentificadorCorrecto() {
        assertThat(scraper.getNombre()).isEqualTo("farmacity");
    }

    @Test
    @DisplayName("buscar() con query válida devuelve al menos un resultado")
    void buscar_queryValida_devuelveResultados() {
        List<ProductoSchema> resultados = scraper.buscar("shampoo");

        assertThat(resultados)
                .as("Farmacity debería devolver resultados para 'shampoo'")
                .isNotEmpty();
    }

    @Test
    @DisplayName("Todos los productos tienen los campos obligatorios completos")
    void buscar_todosLosProductosTienenCamposObligatorios() {
        List<ProductoSchema> resultados = scraper.buscar("shampoo");

        assertThat(resultados).isNotEmpty();
        assertThat(resultados).allSatisfy(p -> {
            assertThat(p.getNombre()).as("nombre no puede ser null ni vacío").isNotBlank();
            assertThat(p.getSource()).as("source debe ser 'farmacity'").isEqualTo("farmacity");
            assertThat(p.getPrecio()).as("precio debe ser >= 0").isGreaterThanOrEqualTo(0.0);
        });
    }

    @Test
    @DisplayName("Todos los productos tienen precio mayor a cero")
    void buscar_todosLosProductosTienenPrecioPositivo() {
        List<ProductoSchema> resultados = scraper.buscar("shampoo");

        assertThat(resultados).isNotEmpty();
        assertThat(resultados)
                .extracting(ProductoSchema::getPrecio)
                .allMatch(precio -> precio > 0, "todos los precios deben ser > 0");
    }

    @Test
    @DisplayName("Todos los productos tienen link con el dominio de Farmacity")
    void buscar_productosConLinkValido() {
        List<ProductoSchema> resultados = scraper.buscar("shampoo");

        assertThat(resultados).isNotEmpty();
        assertThat(resultados)
                .extracting(ProductoSchema::getLink)
                .allMatch(link -> link != null && link.contains("farmacity.com"),
                        "todos los links deben apuntar a farmacity.com");
    }

    @Test
    @DisplayName("buscar() con query sin resultados devuelve lista vacía sin lanzar excepción")
    void buscar_querySinResultados_devuelveListaVaciaSinExplotar() {
        List<ProductoSchema> resultados = scraper.buscar("xkqzwmproductoinexistente9999");

        assertThat(resultados).isNotNull();
    }

    @Test
    @DisplayName("precioLista es siempre mayor o igual al precio")
    void buscar_precioListaSiempreMayorOIgualAlPrecio() {
        List<ProductoSchema> resultados = scraper.buscar("shampoo");

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
        List<ProductoSchema> resultados = scraper.buscar("shampoo");

        assertThat(resultados).isNotEmpty();
        assertThat(resultados)
                .extracting(ProductoSchema::getActualizado)
                .allMatch(fecha -> fecha != null, "todos los productos deben tener fecha");
    }
}