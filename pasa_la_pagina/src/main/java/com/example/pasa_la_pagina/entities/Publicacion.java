package com.example.pasa_la_pagina.entities;

import java.time.LocalDateTime;

import com.example.pasa_la_pagina.entities.Enum.TipoOferta;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Publicacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "disponible", nullable = false)
    private boolean disponible;

    @Column(name = "digital", nullable = false)
    private boolean digital;    

    @Column(name = "latitud", nullable = false)
    private Double latitud;

    @Column(name = "longitud", nullable = false)
    private Double longitud;

    @Column(name = "precio", nullable = true)
    private Double precio;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fecha_creacion;

    @Column(name = "tipo_oferta", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoOferta tipo_oferta;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name= "material_id", nullable = false)
    Material material;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
