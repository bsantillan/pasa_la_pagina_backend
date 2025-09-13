package com.example.pasa_la_pagina.DTOs.requests;

import java.util.List;

import com.example.pasa_la_pagina.entities.Enum.NivelEducativo;
import com.example.pasa_la_pagina.entities.Enum.TipoOferta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PublicacionApunteRequest {
    @NotBlank(message = "El titulo es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripccion es obligatoria")
    private String descripcion;

    @NotNull(message = "El si es nuevo es obligatorio")
    private boolean nuevo;

    @NotNull(message = "El si es digital es obligatorio")
    private boolean digital;

    @NotNull(message = "La latitud es obligatoria")
    private Double latitud;

    @NotNull(message = "La longitud es obligatoria")
    private Double longitud;

    @NotBlank(message = "El idioma es obligatorio")
    private String idioma;

    @Positive(message = "El precio tiene que ser mayor que 0")
    private Double precio;

    @NotNull(message = "La cantidad de material es obligatorio")
    @Positive(message = "La cantidad tiene que ser mayor que 0")
    private Integer cantidad;

    @NotNull(message = "El anio de elaboracion es obligatorio")
    @Positive(message = "El anio de elaboracion tiene que ser mayor que 0")
    private Integer anio_elaboracion;

    @NotNull(message = "El tipo de oferta es obligatorio")
    private TipoOferta tipo_oferta;

    @NotEmpty(message = "Las URLs de las fotos son obligatorias")
    private List<String> fotos_url;

    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;
    
    @NotNull(message = "La cantidad de paginas es obligatorio")
    @Positive(message = "La cantidad de paginas tiene que ser mayor que 0")
    private Integer cantidad_paginas;

    @NotNull(message = "La materia es obligatoria")
    private String materia;

    @NotNull(message = "La institucion es obligatoria")
    private String institucion;

    @NotNull(message = "El nivel educativo es obligatorio")
    private NivelEducativo nivel_educativo;

    @NotNull(message = "La seccion es obligatoria")
    private String seccion;

    private String carrera;

}
