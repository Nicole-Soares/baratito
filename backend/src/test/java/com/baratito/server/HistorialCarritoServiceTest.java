package com.baratito.server;

import com.baratito.server.model.CarritoItem;
import com.baratito.server.model.HistorialCarrito;
import com.baratito.server.model.Usuario;
import com.baratito.server.persistence.interfaces.UsuarioRepository;
import com.baratito.server.persistence.sql.CarritoSQLDAO;
import com.baratito.server.persistence.sql.HistorialCarritoSQLDAO;
import com.baratito.server.security.TokenService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class HistorialCarritoServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CarritoSQLDAO carritoSQLDAO;

    @Autowired
    private HistorialCarritoSQLDAO historialCarritoSQLDAO;

    @Autowired
    private TokenService tokenService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {

        historialCarritoSQLDAO.deleteAll();
        carritoSQLDAO.deleteAll();

        usuario = new Usuario();
        usuario.setNombre("Franco");
        usuario.setEmail("franco@test.com");
        usuario.setPassword("1234");

        usuario = usuarioRepository.save(usuario);

        CarritoItem item = new CarritoItem();

        item.setUsuario(usuario);
        item.setProductoId(1L);
        item.setNombre("Yerba Playadito");
        item.setPrecio(4500);
        item.setCantidad(1);
        item.setSource("coto");

        carritoSQLDAO.save(item);
    }

    @Test
    @DisplayName("Logout mueve productos al historial y vacía el carrito")
    void testLogoutGuardaHistorialYVacíaCarrito() throws Exception {

        String token =
                tokenService.generateToken(usuario.getId());

        mockMvc.perform(
                        post("/api/user/logout")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk());

        List<CarritoItem> carrito =
                carritoSQLDAO.findByUsuario_Id(usuario.getId());

        assertTrue(carrito.isEmpty());

        List<HistorialCarrito> historial =
                historialCarritoSQLDAO.findByUsuario_Id(usuario.getId());

        assertEquals(1, historial.size());

        HistorialCarrito producto = historial.get(0);

        assertEquals(
                "Yerba Playadito",
                producto.getNombre()
        );

        assertEquals(
                1L,
                producto.getProductoId()
        );
    }

    @Test
    @DisplayName("Logout no duplica productos ya existentes en historial")
    void testLogoutNoDuplicaHistorial() throws Exception {

        HistorialCarrito historial = new HistorialCarrito();

        historial.setUsuario(usuario);
        historial.setProductoId(1L);
        historial.setNombre("Yerba Playadito");

        historialCarritoSQLDAO.save(historial);

        String token =
                tokenService.generateToken(usuario.getId());

        mockMvc.perform(
                        post("/api/user/logout")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk());

        List<HistorialCarrito> productos =
                historialCarritoSQLDAO.findByUsuario_Id(usuario.getId());

        assertEquals(1, productos.size());
    }
}