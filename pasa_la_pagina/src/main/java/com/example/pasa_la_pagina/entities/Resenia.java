package com.example.pasa_la_pagina.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resenias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Resenia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion;

    private Integer valoracion;

    // Usuario que escribió la reseña
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id")
    private Usuario autor;

    // Relación con el intercambio
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intercambio_id", nullable = false)
    private Intercambio intercambio;
}
