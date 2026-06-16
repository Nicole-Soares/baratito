package com.baratito.server.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "historial_carrito",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"usuario_id", "producto_id"})}
)
@Getter
@Setter
@NoArgsConstructor
public class HistorialCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    private String nombre;

    private String source;

    private double precio;

    private String imagen;
}
