package com.baratito.server.service;

import com.baratito.server.model.CarritoItem;
import com.baratito.server.model.ProductoSchema;
import com.baratito.server.persistence.sql.ProductoSQLDAO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CarritoService {

    private final List<CarritoItem> items = new ArrayList<>();
    private final ProductoSQLDAO productoSQLDAO;

    public CarritoService(ProductoSQLDAO productoSQLDAO) {
        this.productoSQLDAO = productoSQLDAO;
    }

    public List<CarritoItem> getItems() {
        return items;
    }

    public double getTotal() {
        return items.stream()
                .mapToDouble(i -> i.getPrecio() * i.getCantidad())
                .sum();
    }

    public void agregar(Long productoId) {
        Optional<CarritoItem> existente = items.stream()
                .filter(i -> i.getId().equals(productoId))
                .findFirst();

        if (existente.isPresent()) {
            existente.get().setCantidad(existente.get().getCantidad() + 1);
        } else {
            ProductoSchema p = productoSQLDAO.findById(productoId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productoId));
            items.add(new CarritoItem(p.getId(), p.getNombre(), p.getSource(), p.getPrecio(), p.getImagen()));
        }
    }

    public void quitar(Long productoId) {
        items.removeIf(i -> i.getId().equals(productoId));
    }
}