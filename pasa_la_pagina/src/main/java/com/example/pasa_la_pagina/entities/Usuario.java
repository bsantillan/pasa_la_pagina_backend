package com.example.pasa_la_pagina.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Usuarios")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    
    private String apellido;
    
    @Column(unique = true, nullable = false)
    private String email;

    private String password_hash;

    private String provider;
}
