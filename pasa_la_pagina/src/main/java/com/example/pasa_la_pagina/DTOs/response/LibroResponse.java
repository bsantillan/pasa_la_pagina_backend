package com.example.pasa_la_pagina.DTOs.response;

import java.util.List;

import lombok.Data;

@Data
public class LibroResponse {
    private Long id;
    private String titulo;
    private String idioma;
    private List<String> url_fotos;

    private String isbn;
    private String editorial;
    private String autor;
}
