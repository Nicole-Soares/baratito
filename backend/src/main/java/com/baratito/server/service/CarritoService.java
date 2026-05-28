package com.baratito.server.service;

import com.baratito.server.model.CarritoItem;
import com.baratito.server.model.ProductoSchema;
import com.baratito.server.persistence.interfaces.CarritoRepository;
import com.baratito.server.persistence.sql.ProductoSQLDAO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ProductoSQLDAO productoSQLDAO;

    public CarritoService(CarritoRepository carritoRepository, ProductoSQLDAO productoSQLDAO) {
        this.carritoRepository = carritoRepository;
        this.productoSQLDAO = productoSQLDAO;
    }

    public List<CarritoItem> getItems() {
        return carritoRepository.findAll();
    }

    public double getTotal() {
        return carritoRepository.findAll().stream()
                .mapToDouble(i -> i.getPrecio() * i.getCantidad())
                .sum();
    }

    public void agregar(Long productoId) {
        //  Buscamos usando findByProductoId, NO findById, para poder ver si el producto ya existe (no es lo mismo el id de la fila que del producto)
        CarritoItem item = carritoRepository.findByProductoId(productoId).orElse(null);

        if (item != null) {
            // Si ya existe en el carrito, incrementamos la cantidad del mismo registro
            item.setCantidad(item.getCantidad() + 1);
            carritoRepository.save(item);
        } else {
            // Si no existe, buscamos el producto real en el DAO y creamos un registro nuevo
            ProductoSchema p = productoSQLDAO.findById(productoId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productoId));
            carritoRepository.save(new CarritoItem(
                    p.getId(),
                    p.getNombre(),
                    p.getSource(),
                    p.getPrecio(),
                    p.getImagen()
            ));
        }
    }



    public void decrementar(Long productoId) { //
        // Buscamos por producto_id, no por la clave primaria de la tabla
        CarritoItem item = carritoRepository.findByProductoId(productoId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        int nuevaCantidad = item.getCantidad() - 1;

        if (nuevaCantidad <= 0) {
            carritoRepository.delete(item);
        } else {
            item.setCantidad(nuevaCantidad);
            carritoRepository.save(item);
        }
    }

}
