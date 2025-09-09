package com.example.pasa_la_pagina.DTOs.requests;

import com.example.pasa_la_pagina.entities.Enum.TipoOferta;

import lombok.Data;

@Data
public class MaterialRequest {
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
