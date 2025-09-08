package com.example.pasa_la_pagina.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "libro")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Libro {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String isbn;

    @ManyToOne @JoinColumn(name = "foto_id")
    private Foto foto;

    @ManyToOne @JoinColumn(name = "editorial_id")
    private Editorial editorial;

    @ManyToOne @JoinColumn(name = "genero_id")
    private Genero genero;

    @ManyToOne @JoinColumn(name = "autor_id")
    private Autor autor;
}
