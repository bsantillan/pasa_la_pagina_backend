package com.example.pasa_la_pagina.entities;

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

    @Column(name = "latitud", nullable = false, precision = 10, scale = 6)
    private Double latitud;

    @Column(name = "longitud", nullable = false, precision = 10, scale = 6)
    private Double longitud;

    @Column(name = "precio", nullable = true, precision = 10, scale = 2)
    private Double precio;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "tipo_oferta", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoOferta tipoOferta;

    @OneToOne
    @JoinColumn(name= "material_id", nullable = false)
    Material material;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
