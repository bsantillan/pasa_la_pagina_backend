package com.example.pasa_la_pagina.DTOs.response;

import java.util.List;

import com.example.pasa_la_pagina.entities.Enum.NivelEducativo;
import com.example.pasa_la_pagina.entities.Enum.TipoOferta;

import lombok.Data;

@Data
public class PublicacionApunteResponse {
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
    private Integer cantidad;
    private TipoOferta tipo_oferta;
    private List<String> url_fotos;
    private Integer cantidad_paginas;
    private Integer anio_elaboracion;
    private String materia;
    private String institucion;
    private NivelEducativo nivel_educativo;
    private String seccion;
    private String carrera;

}
