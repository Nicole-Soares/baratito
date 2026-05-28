package com.baratito.server;

import com.baratito.server.model.ProductoSchema;
import com.baratito.server.persistence.interfaces.CacheRepository;
import com.baratito.server.persistence.interfaces.ProductoRepository;
import com.baratito.server.scraper.ScraperMaster;
import com.baratito.server.service.ProductoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest // Carga el contexto real de la app (incluyendo Postgres)
@Transactional // Permite manejar la transacción,



class ProductoServiceTest {

    static {
        System.setProperty("user.timezone", "UTC");
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
    }

    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private CacheRepository cacheRepository;

    @Mock
    private ScraperMaster scraperMaster; // Scraper mockeado

    private ProductoService productoService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        productoRepository.deleteAll();

        cacheRepository.deleteAll();

        productoService = new ProductoService(scraperMaster, productoRepository, cacheRepository);
    }

    @Test
    @DisplayName("Debe persistir productos en Postgres cuando la base está vacía y lo trae del scraper")
    void testPersistenciaRealEnPostgres() {

        String query = "yerba";

        List<ProductoSchema> resultadosFalsosDelScraper = List.of(
                new ProductoSchema("coto", "Yerba Playadito 1kg", "http://coto.com/playadito", "img_url", true, 4500.0, 5000.0, LocalDate.now()),
                new ProductoSchema("dia", "Yerba Mañanita 1kg", "http://dia.com/mananita", "img_url", true, 3800.0, 4000.0, LocalDate.now())
        );

        // Cuando el service pregunte al scraper, devolvemos esta lista
        when(scraperMaster.buscarEnTodos(query)).thenReturn(resultadosFalsosDelScraper);

        // WHEN
        // El service verá que la base está vacía (o vieja) y llamará al repo real para guardar
        List<ProductoSchema> resultado = productoService.buscarProductos(query);

        // THEN
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());

        // Verificación de ordenamiento por precio
        assertEquals("dia", resultado.get(0).getSource());

        //chequear que estan en la base
        List<ProductoSchema> productosEnBD = productoRepository.findAll();

        assertEquals(2, productosEnBD.size());

    //  Verificamos que los nombres coincidan (validamos que se guardó bien)
        boolean guardoPlayadito = productosEnBD.stream()
                .anyMatch(p -> p.getNombre().contains("Playadito"));
        assertTrue(guardoPlayadito);

    }

    @Test
    @DisplayName("Debe encontrar productos con una búsqueda difusa por error ortográfico incluso si no fueron actualizados hoy")
    void testBusquedaDifusaPorErrorOrtograficoConDatosHistoricos() {

        String queryConError = "leche zancor";

        ProductoSchema producto = new ProductoSchema(
                "coto",
                "Leche Sancor 1L",
                "https://coto.example/sancor",
                "img",
                true,
                1200.0,
                1300.0,
                LocalDate.now().minusDays(5)
        );

        productoRepository.saveAllYObtenerOrdenados(List.of(producto), "leche sancor");

        List<ProductoSchema> resultados = productoRepository.encontrarProductos(queryConError);

        assertFalse(resultados.isEmpty());
        assertTrue(resultados.stream().anyMatch(p -> p.getNombre().contains("Sancor")));
    }

    @Test
    @DisplayName("Debe actualiza la fecha un producto existente (Upsert) por estar desactualizado")
    void testUpsertReal() {

        String query = "leche";

        //  Guardamos un producto desactualizado
        ProductoSchema productoDesactualizado = new ProductoSchema("coto", "Leche", "link", "img", true, 1000.0, 1000.0, LocalDate.now().minusDays(7));
        productoRepository.saveAllYObtenerOrdenados(List.of(productoDesactualizado), query);

        //chequeamos que se guardo
        assertNotNull(productoRepository.findAll());

        // 2. Simulamos que el scraper lo encuentra más barato
        ProductoSchema actualizado = new ProductoSchema("coto", "Leche", "link", "img", true, 850.0, 1000.0, LocalDate.now());
        when(scraperMaster.buscarEnTodos(query)).thenReturn(List.of(actualizado));

        // buscamos el producto
       List<ProductoSchema> productos =  productoService.buscarProductos(query);

        //chequear que estan en la base
        List<ProductoSchema> productosEnBD = productoRepository.findAll();

        //chequeado que no se duplico
        assertEquals(1, productosEnBD.size());
        assertEquals(1, productos.size());

        //se actualizo la fecha en la bd
        assertEquals(LocalDate.now(), productosEnBD.get(0).getActualizado());
    }



}

