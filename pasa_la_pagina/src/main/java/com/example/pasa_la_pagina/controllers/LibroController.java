package com.example.pasa_la_pagina.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pasa_la_pagina.DTOs.requests.PublicacionLibroRequest;
import com.example.pasa_la_pagina.DTOs.response.PublicacionLibroResponse;
import com.example.pasa_la_pagina.services.LibroService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class LibroController {

    private final LibroService libroService;
    
    @PostMapping("/new")
    public ResponseEntity<PublicacionLibroResponse> nuevoLibro(@Valid @RequestBody PublicacionLibroRequest request){
        return ResponseEntity.ok(libroService.nuevoLibro(request));
    }
}
