package com.example.pasa_la_pagina.entities;

import jakarta.persistence.*;
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

    // Dueña de la relación 1-1 con Intercambio
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "intercambio_id",
        nullable = false,
        unique = true,
        foreignKey = @ForeignKey(name = "fk_resenia_intercambio")
    )
    private Intercambio intercambio;
}