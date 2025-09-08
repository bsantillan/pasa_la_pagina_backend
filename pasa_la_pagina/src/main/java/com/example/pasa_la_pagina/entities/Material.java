package com.example.pasa_la_pagina.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "material")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @ManyToOne
    @JoinColumn(name = "grado_id")
    private Grado grado;

    @ManyToOne
    @JoinColumn(name = "materia_id")
    private Materia materia;

    @ManyToOne
    @JoinColumn(name = "carrera_id")
    private Carrera carrera;

    @ManyToOne
    @JoinColumn(name = "intitucion_id")
    private Institucion institucion;

    public enum TipoOferta {
        Venta,
        Intercambio,
        Donacion
    }
}
