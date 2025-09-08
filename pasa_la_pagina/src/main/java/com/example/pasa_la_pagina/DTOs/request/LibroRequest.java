package com.example.pasa_la_pagina.DTOs.requests;
import lombok.Data;
@Data
public class LibroRequest {
    private String isbn;
    private Long fotoId;
    private Long editorialId;
    private Long generoId;
    private Long autorId;
}
