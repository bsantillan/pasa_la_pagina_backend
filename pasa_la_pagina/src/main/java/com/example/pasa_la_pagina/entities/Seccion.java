package com.example.pasa_la_pagina.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Seccion")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Seccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 30)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materia;

    @ManyToOne
    @JoinColumn(name = "institucion_id", nullable = false)
    private Institucion institucion;

    @ManyToOne
    @JoinColumn(name = "carrera_id", nullable = true)
    private Carrera carrera;
}
