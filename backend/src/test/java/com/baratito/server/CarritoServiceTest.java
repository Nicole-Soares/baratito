package com.baratito.server;
import com.baratito.server.model.CarritoItem;
import com.baratito.server.model.ProductoSchema;
import com.baratito.server.persistence.interfaces.CarritoRepository;
import com.baratito.server.persistence.sql.ProductoSQLDAO;
import com.baratito.server.service.CarritoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class CarritoServiceTest {

    static {
        System.setProperty("user.timezone", "UTC");
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
    }

    private CarritoItem carritoItem;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ProductoSQLDAO productoSQLDAO;

    @Autowired
    private CarritoService carritoService;

    private ProductoSchema productoSchema;
    private ProductoSchema productoSchema2;

    @BeforeEach
    void setUp() {
        // 1. Limpiamos las tablas para asegurar un entorno controlado
        carritoRepository.deleteAll();
        productoSQLDAO.deleteAll();

        // 2. Instanciamos los productos usando tus datos reales
        productoSchema = new ProductoSchema("coto", "Yerba Playadito 1kg", "http://coto.com/playadito", "img_url", true, 4500.0, 5000.0, LocalDate.now());
        productoSchema2 = new ProductoSchema("dia", "Yerba Mañanita 1kg", "http://dia.com/mananita", "img_url", true, 3800.0, 4000.0, LocalDate.now());


        productoSchema = productoSQLDAO.save(productoSchema);
        productoSchema2 = productoSQLDAO.save(productoSchema2);
    }


    @Test
    @DisplayName("Debería retornar todos los ítems del carrito")
    void testGetItems() {

        carritoService.agregar(productoSchema.getId());
        List<CarritoItem> resultado = carritoService.getItems();

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("Yerba Playadito 1kg", resultado.get(0).getNombre());
    }

    @Test
    @DisplayName("Debería calcular el total correctamente basándose en precio y cantidad")
    void testGetTotal() {

        carritoService.agregar(productoSchema2.getId());

        double total = carritoService.getTotal();

        assertEquals(3800.0, total);
    }


    @Test
    @DisplayName("Agregar: Si el ítem ya existe, debería incrementar su cantidad en 1")
    void testAgregarItemExistente() {


        carritoService.agregar(productoSchema2.getId());
        carritoService.agregar(productoSchema2.getId());
        List<CarritoItem> resultados = carritoService.getItems();

        // Assert
        assertEquals(2, resultados.get(0).getCantidad());
    }

    @Test
    @DisplayName("Agregar: Si el ítem no existe, debería buscar el producto y crear un nuevo CarritoItem")
    void testAgregarItemNuevo() {
        // Act - Agregamos el producto que guardamos en el setUp()
        carritoService.agregar(productoSchema.getId());

        List<CarritoItem> resultados = carritoService.getItems();

        // Assert
        assertEquals(1, resultados.size());
        CarritoItem itemGuardado = resultados.get(0);
        assertEquals(productoSchema.getId(), itemGuardado.getProductoId());
        assertEquals("Yerba Playadito 1kg", itemGuardado.getNombre());
        assertEquals(4500.0, itemGuardado.getPrecio());
    }

    @Test
    @DisplayName("Agregar: Si el ítem no existe y el producto tampoco, debería lanzar RuntimeException")
    void testAgregarItemNuevoProductoNoEncontrado() {
        Long productoIdInexistente = 999L;

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            carritoService.agregar(productoIdInexistente);
        });

        assertTrue(exception.getMessage().contains("Producto no encontrado"));
    }

    @Test
    @DisplayName("Decrementar: Si la cantidad es mayor a 1, disminuye la cantidad y guarda")
    void testDecrementarReduceCantidad() {
        // Arrange - Agregamos el producto 2 veces para que la cantidad inicial sea 2
        carritoService.agregar(productoSchema.getId());
        carritoService.agregar(productoSchema.getId());

        // Act - Decrementamos una vez
        carritoService.decrementar(productoSchema.getId());

        List<CarritoItem> resultados = carritoService.getItems();

        // Assert - Debería quedar 1 solo elemento en cantidad
        assertEquals(1, resultados.get(0).getCantidad());
    }

    @Test
    @DisplayName("Decrementar: Si la cantidad llega a 0, elimina el ítem del repositorio")
    void testDecrementarEliminaItem() {
        // Arrange - Agregamos el producto 1 vez
        carritoService.agregar(productoSchema.getId());

        // Act - Decrementamos para que llegue a 0
        carritoService.decrementar(productoSchema.getId());

        List<CarritoItem> resultados = carritoService.getItems();

        // Assert - El carrito debería quedar completamente vacío
        assertTrue(resultados.isEmpty());
    }

    @Test
    @DisplayName("Decrementar: Si el ítem no existe, lanza RuntimeException")
    void testDecrementarItemNoEncontrado() {
        Long idInexistente = 999L;

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            carritoService.decrementar(idInexistente);
        });
    }
}
