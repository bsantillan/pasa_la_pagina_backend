package com.example.pasa_la_pagina.DTOs.requests;

import java.util.List;

import com.example.pasa_la_pagina.entities.Enum.NivelEducativo;
import com.example.pasa_la_pagina.entities.Enum.TipoMaterial;
import com.example.pasa_la_pagina.entities.Enum.TipoOferta;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatePublicacionRequest {
    @NotNull(message = "El id de la publicacion es obligatorio")
    private Long id;
    private String titulo;
    private String descripcion;
    private Boolean nuevo;
    private Boolean disponible;
    private Boolean digital;
    private Double latitud;
    private Double longitud;
    private String idioma;
    private Double precio;
    private Integer cantidad;
    private TipoOferta tipo_oferta;
    private List<String> url_fotos;

    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El tipo de material es obligatorio")
    private TipoMaterial tipo_material;

    // Campos de Apunte
    private Integer cantidad_paginas;
    private Integer anio_elaboracion;
    private String materia;
    private String institucion;
    private NivelEducativo nivel_educativo;
    private String seccion;
    private String carrera;

    // Campos de Libro
    private String isbn;
    private String editorial;
    private String genero;
    private String autor;
}
