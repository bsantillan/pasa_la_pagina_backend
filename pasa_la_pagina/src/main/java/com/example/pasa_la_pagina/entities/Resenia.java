package com.example.pasa_la_pagina.entities;

import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reseniador_id")
    private Usuario reseniador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reseniado_id")
    private Usuario reseniado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intercambio_id", nullable = false)
    private Intercambio intercambio;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
}
