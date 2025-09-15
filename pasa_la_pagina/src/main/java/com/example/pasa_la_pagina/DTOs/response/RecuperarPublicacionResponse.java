package com.example.pasa_la_pagina.DTOs.response;

import java.util.List;

import com.example.pasa_la_pagina.entities.Enum.NivelEducativo;
import com.example.pasa_la_pagina.entities.Enum.TipoMaterial;
import com.example.pasa_la_pagina.entities.Enum.TipoOferta;

import lombok.Data;

@Data
public class RecuperarPublicacionResponse {
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
    private Long usuario_id;
    private String usuario_nombre;
    private String usuario_apellido;

    private TipoMaterial tipo_material;

    private Integer cantidad_paginas;
    private Integer anio_elaboracion;
    private String materia;
    private String institucion;
    private NivelEducativo nivel_educativo;
    private String seccion;
    private String carrera;

    private String isbn;
    private String editorial;
    private String genero;
    private String autor;

}
