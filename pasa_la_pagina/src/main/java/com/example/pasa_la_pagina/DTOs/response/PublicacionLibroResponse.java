package com.example.pasa_la_pagina.DTOs.response;

import java.util.List;

import com.example.pasa_la_pagina.entities.Enum.TipoOferta;

import lombok.Data;

@Data
public class PublicacionLibroResponse {
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
    private Integer cantidad;
    private String isbn;
    private List<String> fotos;
    private String editorial;
    private String genero;
    private String autor;
}
