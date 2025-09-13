package com.example.pasa_la_pagina.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pasa_la_pagina.DTOs.requests.PublicacionApunteRequest;
import com.example.pasa_la_pagina.DTOs.requests.PublicacionLibroRequest;
import com.example.pasa_la_pagina.DTOs.response.PublicacionApunteResponse;
import com.example.pasa_la_pagina.DTOs.response.PublicacionLibroResponse;
import com.example.pasa_la_pagina.services.PublicacionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/publicacion")
@RequiredArgsConstructor
public class PublicacionController {

    private final PublicacionService publicacionService;
    
    @PostMapping("/nuevo/libro")
    public ResponseEntity<PublicacionLibroResponse> nuevaPublicacionLibro(@Valid @RequestBody PublicacionLibroRequest request){
        return ResponseEntity.ok(publicacionService.nuevaPublicacionLibro(request));
    }

    @PostMapping("/nuevo/apunte")
    public ResponseEntity<PublicacionApunteResponse> nuevaPublicacionApunte(@Valid @RequestBody PublicacionApunteRequest request){
        return ResponseEntity.ok(publicacionService.nuevaPublicacionApunte(request));
    }
}
