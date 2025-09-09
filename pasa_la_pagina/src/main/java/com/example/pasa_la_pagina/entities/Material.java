package com.example.pasa_la_pagina.entities;


import com.example.pasa_la_pagina.entities.Enum.TipoOferta;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "material")
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

    private String titulo;
    private String descripcion;

    private boolean nuevo;
    private boolean disponible;
    private boolean digital;    

    private Double latitud;
    private Double longitud;

    private String idioma;
    private Double precio;

    @Enumerated(EnumType.STRING)
    private TipoOferta tipoOferta;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
