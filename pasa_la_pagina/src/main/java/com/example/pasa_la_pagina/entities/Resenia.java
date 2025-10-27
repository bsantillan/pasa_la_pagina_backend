package com.example.pasa_la_pagina.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;

@Entity
@Table(name = "Resenia")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Resenia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private Integer valoracion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "intercambio_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_resenia_intercambio")
    )
    private Intercambio intercambio;

    // Usuario que escribió la reseña
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "usuario_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_resenia_usuario")
    )
    private Usuario usuario;

    // Fecha de creación
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // Validación: el usuario debe ser solicitante o propietario del intercambio
    @AssertTrue(message = "El usuario de la reseña debe ser el solicitante o el propietario del intercambio.")
    private boolean isUsuarioValido() {
        if (usuario == null || intercambio == null || intercambio.getSolicitante() == null || intercambio.getPropietario() == null) {
            return true; 
        }
        Long userId = usuario.getId();
        return userId.equals(intercambio.getSolicitante().getId()) || userId.equals(intercambio.getPropietario().getId());
    }


}