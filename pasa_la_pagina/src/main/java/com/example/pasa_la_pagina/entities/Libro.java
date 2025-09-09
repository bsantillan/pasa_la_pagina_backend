package com.example.pasa_la_pagina.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "libro")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@SuperBuilder
@PrimaryKeyJoinColumn(name = "id") 
public class Libro extends Material{

    @Column(name = "isbn", nullable = false, length = 20)
    private String isbn;

    /* 
    @ManyToOne @JoinColumn(name = "foto_id")
    private Foto foto;

    @ManyToOne @JoinColumn(name = "editorial_id")
    private Editorial editorial;

    @ManyToOne @JoinColumn(name = "genero_id")
    private Genero genero;

    @ManyToOne @JoinColumn(name = "autor_id")
    private Autor autor;
    */
}
