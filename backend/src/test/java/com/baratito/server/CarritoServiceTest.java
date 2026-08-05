package com.baratito.server;
import com.baratito.server.model.CarritoItem;
import com.baratito.server.model.ProductoSchema;
import com.baratito.server.model.Usuario;
import com.baratito.server.persistence.interfaces.CarritoRepository;
import com.baratito.server.persistence.interfaces.UsuarioRepository;
import com.baratito.server.persistence.sql.ProductoSQLDAO;
import com.baratito.server.service.CarritoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CarritoServiceTest {

    static {
        System.setProperty("user.timezone", "UTC");
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
    }

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ProductoSQLDAO productoSQLDAO;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CarritoService carritoService;

    private ProductoSchema productoSchema;
    private ProductoSchema productoSchema2;
    private Long usuarioId;

    @BeforeEach
    void setUp() {
        // 1. Limpiamos las tablas para asegurar un entorno controlado
        carritoRepository.deleteAll();
        productoSQLDAO.deleteAll();

        // 2. Creamos un usuario de prueba
        Usuario usuario = new Usuario();
        usuario.setNombre("Test User");
        usuario.setEmail("test@baratito.com");
        usuario.setPassword("password123");
        usuario = usuarioRepository.save(usuario);
        usuarioId = usuario.getId();

        // 3. Instanciamos los productos usando tus datos reales
        productoSchema = new ProductoSchema("coto", "Yerba Playadito 1kg", "http://coto.com/playadito", "img_url", true, 4500.0, 5000.0, LocalDate.now());
        productoSchema2 = new ProductoSchema("dia", "Yerba Mañanita 1kg", "http://dia.com/mananita", "img_url", true, 3800.0, 4000.0, LocalDate.now());

        productoSchema = productoSQLDAO.save(productoSchema);
        productoSchema2 = productoSQLDAO.save(productoSchema2);
    }


    @Test
    @DisplayName("Debería retornar todos los ítems del carrito")
    void testGetItems() {

        carritoService.agregar(usuarioId, productoSchema.getId());
        List<CarritoItem> resultado = carritoService.getItems(usuarioId);

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("Yerba Playadito 1kg", resultado.get(0).getNombre());
    }

    @Test
    @DisplayName("Debería calcular el total correctamente basándose en precio y cantidad")
    void testGetTotal() {

        carritoService.agregar(usuarioId, productoSchema2.getId());

        double total = carritoService.getTotal(usuarioId);

        assertEquals(3800.0, total);
    }


    @Test
    @DisplayName("Agregar: Si el ítem ya existe, debería incrementar su cantidad en 1")
    void testAgregarItemExistente() {

        carritoService.agregar(usuarioId, productoSchema2.getId());
        carritoService.agregar(usuarioId, productoSchema2.getId());
        List<CarritoItem> resultados = carritoService.getItems(usuarioId);

        // Assert
        assertEquals(2, resultados.get(0).getCantidad());
    }

    @Test
    @DisplayName("Agregar: Si el ítem no existe, debería buscar el producto y crear un nuevo CarritoItem")
    void testAgregarItemNuevo() {
        // Act - Agregamos el producto que guardamos en el setUp()
        carritoService.agregar(usuarioId, productoSchema.getId());

        List<CarritoItem> resultados = carritoService.getItems(usuarioId);

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
            carritoService.agregar(usuarioId, productoIdInexistente);
        });

        assertTrue(exception.getMessage().contains("Producto no encontrado"));
    }

    @Test
    @DisplayName("Decrementar: Si la cantidad es mayor a 1, disminuye la cantidad y guarda")
    void testDecrementarReduceCantidad() {
        // Arrange - Agregamos el producto 2 veces para que la cantidad inicial sea 2
        carritoService.agregar(usuarioId, productoSchema.getId());
        carritoService.agregar(usuarioId, productoSchema.getId());

        // Act - Decrementamos una vez
        carritoService.decrementar(usuarioId, productoSchema.getId());

        List<CarritoItem> resultados = carritoService.getItems(usuarioId);

        // Assert - Debería quedar 1 solo elemento en cantidad
        assertEquals(1, resultados.get(0).getCantidad());
    }

    @Test
    @DisplayName("Decrementar: Si la cantidad llega a 0, elimina el ítem del repositorio")
    void testDecrementarEliminaItem() {
        // Arrange - Agregamos el producto 1 vez
        carritoService.agregar(usuarioId, productoSchema.getId());

        // Act - Decrementamos para que llegue a 0
        carritoService.decrementar(usuarioId, productoSchema.getId());

        List<CarritoItem> resultados = carritoService.getItems(usuarioId);

        // Assert - El carrito debería quedar completamente vacío
        assertTrue(resultados.isEmpty());
    }

    @Test
    @DisplayName("Decrementar: Si el ítem no existe, lanza RuntimeException")
    void testDecrementarItemNoEncontrado() {
        Long idInexistente = 999L;

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            carritoService.decrementar(usuarioId, idInexistente);
        });
    }
}

