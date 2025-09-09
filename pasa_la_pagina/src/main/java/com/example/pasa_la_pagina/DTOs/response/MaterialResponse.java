package com.example.pasa_la_pagina.DTOs.response;

import com.example.pasa_la_pagina.entities.Enum.TipoOferta;

import lombok.Data;

@Data
public class MaterialResponse {
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
    private TipoOferta tipoOferta;
    private Long usuarioId;
    private Long gradoId;
    private Long materiaId;
    private Long carreraId;
    private Long institucionId;
}
