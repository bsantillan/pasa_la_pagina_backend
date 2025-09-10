package com.example.pasa_la_pagina.entities;

import java.util.List;

import com.example.pasa_la_pagina.entities.Enum.NivelEducativo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Institucion")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Institucion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "nivel_educativo", nullable = false)
    @Enumerated(EnumType.STRING)
    private NivelEducativo nivel_educativo;

    @OneToMany(mappedBy = "institucion")
    private List<Seccion> secciones;
}
