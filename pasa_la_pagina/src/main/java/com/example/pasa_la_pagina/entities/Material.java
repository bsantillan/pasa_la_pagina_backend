package com.example.pasa_la_pagina.entities;


import com.example.pasa_la_pagina.entities.Enum.TipoOferta;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "Material")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo", nullable = false, length = 100)
    private String titulo;

    @Column(name = "descripcion", nullable = false, length = 200)
    private String descripcion;

    @Column(name = "nuevo", nullable = false)
    private boolean nuevo;

    @Column(name = "disponible", nullable = false)
    private boolean disponible;

    @Column(name = "digital", nullable = false)
    private boolean digital;    

    @Column(name = "latitud", nullable = false, precision = 10, scale = 6)
    private Double latitud;

    @Column(name = "longitud", nullable = false, precision = 10, scale = 6)
    private Double longitud;

    @Column(name = "idioma", nullable = false, length = 50)
    private String idioma;

    @Column(name = "precio", nullable = true, precision = 10, scale = 2)
    private Double precio;

    @Column(name = "tipo_oferta", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoOferta tipoOferta;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
