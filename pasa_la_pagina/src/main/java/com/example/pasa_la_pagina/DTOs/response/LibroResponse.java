package com.example.pasa_la_pagina.DTOs.response;
import lombok.Data;
@Data
public class LibroResponse {
    private Long id;
    private String isbn;
    private Long fotoId;
    private Long editorialId;
    private Long generoId;
    private Long autorId;
}
