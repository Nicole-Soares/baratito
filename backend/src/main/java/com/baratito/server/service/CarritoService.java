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
        CarritoItem item = carritoRepository.findById(productoId).orElse(null);

        if (item != null) {
            item.setCantidad(item.getCantidad() + 1);
            carritoRepository.save(item);
        } else {
            ProductoSchema p = productoSQLDAO.findById(productoId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productoId));
            carritoRepository.save(new CarritoItem(
                    p.getId(), // 👈 este es el productoId
                    p.getNombre(),
                    p.getSource(),
                    p.getPrecio(),
                    p.getImagen()
            ));
        }
    }



    public void decrementarPorRegistro(Long id) {
        CarritoItem item = carritoRepository.findById(id)
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
