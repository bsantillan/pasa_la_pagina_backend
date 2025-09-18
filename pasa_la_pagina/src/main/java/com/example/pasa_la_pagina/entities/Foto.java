package com.example.pasa_la_pagina.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Foto")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Foto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url", nullable = false, length = 100)
    private String url; 

    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    @JsonBackReference
    private Material material;
}
