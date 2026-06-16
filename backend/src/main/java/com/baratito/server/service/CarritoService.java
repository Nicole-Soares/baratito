package com.baratito.server.service;

import com.baratito.server.model.CarritoItem;
import com.baratito.server.model.HistorialCarrito;
import com.baratito.server.model.ProductoSchema;
import com.baratito.server.model.Usuario;
import com.baratito.server.persistence.interfaces.CarritoRepository;
import com.baratito.server.persistence.interfaces.UsuarioRepository;
import com.baratito.server.persistence.sql.ProductoSQLDAO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

import java.util.List;

@Service
@Transactional
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ProductoSQLDAO productoSQLDAO;
    private final UsuarioRepository usuarioRepository;
    private final HistorialCarritoService historialCarritoService;

    public CarritoService(CarritoRepository carritoRepository, ProductoSQLDAO productoSQLDAO, UsuarioRepository usuarioRepository, HistorialCarritoService historialCarritoService) {
        this.carritoRepository = carritoRepository;
        this.productoSQLDAO = productoSQLDAO;
        this.usuarioRepository = usuarioRepository;
        this.historialCarritoService = historialCarritoService;
    }

    private Long getUsuarioIdActual() {
        return Long.parseLong(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal()
                        .toString()
        );
    }

    public List<CarritoItem> getItems() {
        Long usuarioId = getUsuarioIdActual();
        System.out.println("USUARIO ID = " + usuarioId); //borar
        return carritoRepository.findByUsuarioId(usuarioId);
    }

    public double getTotal() {
        Long usuarioId = getUsuarioIdActual();

        return carritoRepository.findByUsuarioId(usuarioId)
                .stream()
                .mapToDouble(i -> i.getPrecio() * i.getCantidad())
                .sum();
    }

    public void agregar(Long productoId) {

        Long usuarioId = getUsuarioIdActual();

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Busca si ese usuario ya tiene ese producto en el carrito
        CarritoItem item = carritoRepository
                .findByUsuarioIdAndProductoId(usuarioId, productoId)
                .orElse(null);

        if (item != null) {

            // Si ya existe, aumenta la cantidad
            item.setCantidad(item.getCantidad() + 1);
            carritoRepository.save(item);

        } else {

            // Busca el producto real
            ProductoSchema p = productoSQLDAO.findById(productoId)
                    .orElseThrow(() ->
                            new RuntimeException("Producto no encontrado: " + productoId)
                    );

            // Crea un nuevo item del carrito
            CarritoItem nuevoItem = new CarritoItem(
                    p.getId(),
                    p.getNombre(),
                    p.getSource(),
                    p.getPrecio(),
                    p.getImagen()
            );

            // Lo asocia al usuario logueado
            nuevoItem.setUsuario(usuario);

            carritoRepository.save(nuevoItem);
        }
    }

    public void decrementar(Long productoId) {

        Long usuarioId = getUsuarioIdActual();

        CarritoItem item = carritoRepository
                .findByUsuarioIdAndProductoId(usuarioId, productoId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        int nuevaCantidad = item.getCantidad() - 1;

        if (nuevaCantidad <= 0) {
            carritoRepository.delete(item);
        } else {
            item.setCantidad(nuevaCantidad);
            carritoRepository.save(item);
        }
    }

    public void guardarHistorial() {

        Long usuarioId = getUsuarioIdActual();

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<CarritoItem> items = carritoRepository.findByUsuarioId(usuarioId);

        for (CarritoItem item : items) {

            boolean yaExiste = historialCarritoService.existeProducto(
                    usuarioId,
                    item.getProductoId()
            );

            if (!yaExiste) {

                HistorialCarrito historial = new HistorialCarrito();

                historial.setUsuario(usuario);
                historial.setProductoId(item.getProductoId());
                historial.setNombre(item.getNombre());
                historial.setSource(item.getSource());
                historial.setPrecio(item.getPrecio());
                historial.setImagen(item.getImagen());

                historialCarritoService.guardar(historial);
            }
        }

        for (CarritoItem item : items) {
            carritoRepository.delete(item);
        }
    }

}
