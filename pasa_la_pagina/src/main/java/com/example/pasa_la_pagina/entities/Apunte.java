package com.example.pasa_la_pagina.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "apunte")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@SuperBuilder
@PrimaryKeyJoinColumn(name = "id") 
public class Apunte {
    
    @Min(1)
    @Column(name = "cantidad_paginas", nullable = false)
    private Integer cantidad_paginas;

    @Min(1)
    @Column(name = "anio_elaboracion", nullable = false)
    private Integer anio_elaboracion;

    /*
    @ManyToOne
    @JoinColumn(name = "grado_id", nullable = true)
    private Grado grado;

    @ManyToOne
    @JoinColumn(name = "carrera_id", nullable = true)
    private Carrera carrera;

    @ManyToOne
    @JoinColumn(name = "institucion_id", nullable = false)
    private Institucion institucion;

    @ManyToOne
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia meteria;
    */
}
