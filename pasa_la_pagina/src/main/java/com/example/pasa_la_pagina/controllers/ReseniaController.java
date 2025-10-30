package com.example.pasa_la_pagina.controllers;

import com.example.pasa_la_pagina.DTOs.requests.CrearReseniaRequest;
import com.example.pasa_la_pagina.DTOs.response.ReseniaResponse;
import com.example.pasa_la_pagina.services.ReseniaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resenia") // URL base
@RequiredArgsConstructor
public class ReseniaController {

    private final ReseniaService reseniaService;

    // 1️⃣ Crear una reseña
    @PostMapping
    public ResponseEntity<ReseniaResponse> crearResenia(@RequestBody CrearReseniaRequest request) {
        return ResponseEntity.ok(reseniaService.crearResenia(request));
    }

    // 2️⃣ Reseñas recibidas por un usuario
    @GetMapping("/usuario/{id}/recibidas")
    public ResponseEntity<List<ReseniaResponse>> getReseniasRecibidas(@PathVariable Long id) {
        return ResponseEntity.ok(reseniaService.getReseniasRecibidas(id));
    }

    // 3️⃣ Reseñas hechas por un usuario
    @GetMapping("/usuario/{id}/hechas")
    public ResponseEntity<List<ReseniaResponse>> getReseniasHechas(@PathVariable Long id) {
        return ResponseEntity.ok(reseniaService.getReseniasHechas(id));
    }

    // 4️⃣ Reseñas de un intercambio específico
    @GetMapping("/intercambio/{id}")
    public ResponseEntity<List<ReseniaResponse>> getReseniasPorIntercambio(@PathVariable Long id) {
        return ResponseEntity.ok(reseniaService.getReseniasPorIntercambio(id));
    }
}
