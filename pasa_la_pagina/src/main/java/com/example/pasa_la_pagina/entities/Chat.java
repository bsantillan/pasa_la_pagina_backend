package com.example.pasa_la_pagina.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Chat")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private boolean activo = true;

    @Column(length = 100)
    private String titulo;

    // Lado inverso
    // @OneToOne(mappedBy = "chat", fetch = FetchType.LAZY)
    // private Intercambio intercambio;
}