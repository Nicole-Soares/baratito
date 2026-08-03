package com.baratito.server.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Getter
@Setter
@NoArgsConstructor
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "producto_nombre", nullable = false)
    private String productoNombre;

    @Column(name = "producto_imagen")
    private String productoImagen;

    @Column(name = "precio_anterior", nullable = false)
    private double precioAnterior;

    @Column(name = "precio_nuevo", nullable = false)
    private double precioNuevo;

    @Column(name = "leida", nullable = false)
    private boolean leida = false;

    @Column(name = "creada", nullable = false)
    private LocalDateTime creada;

    public Notificacion(Long usuarioId, Long productoId, String productoNombre, String productoImagen,
                        double precioAnterior, double precioNuevo) {
        this.usuarioId = usuarioId;
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.productoImagen = productoImagen;
        this.precioAnterior = precioAnterior;
        this.precioNuevo = precioNuevo;
        this.leida = false;
        this.creada = LocalDateTime.now();
    }
}