package com.example.pasa_la_pagina.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pasa_la_pagina.DTOs.response.LibroResponse;
import com.example.pasa_la_pagina.services.LibroService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/libros")
@RequiredArgsConstructor
public class LibroController {

    private final LibroService libroService;

    @GetMapping("/buscar")
    public ResponseEntity<List<LibroResponse>> buscarLibros(@RequestParam String isbn) {
        return ResponseEntity.ok(libroService.buscarLibros(isbn));
    }
}
