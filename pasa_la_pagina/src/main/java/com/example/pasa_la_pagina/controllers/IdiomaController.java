package com.example.pasa_la_pagina.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pasa_la_pagina.DTOs.response.IdiomaResponse;
import com.example.pasa_la_pagina.services.IdiomaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/idioma")
@RequiredArgsConstructor
public class IdiomaController {
    
    private final IdiomaService idiomaRepository;

    @GetMapping("/")
    public ResponseEntity<List<IdiomaResponse>> nuevaPublicacionLibro() {
        return ResponseEntity.ok(idiomaRepository.findAll());
    }

}
