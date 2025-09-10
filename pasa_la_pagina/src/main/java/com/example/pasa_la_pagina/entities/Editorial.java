package com.example.pasa_la_pagina.entities;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Editorial")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Editorial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100, unique = true)
    private String nombre;
}
