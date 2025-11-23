package com.example.pasa_la_pagina.controllers;

import com.example.pasa_la_pagina.DTOs.requests.CrearReseniaRequest;
import com.example.pasa_la_pagina.DTOs.response.ReseniaResponse;
import com.example.pasa_la_pagina.exceptions.IntercambioInvalidoException;
import com.example.pasa_la_pagina.services.ReseniaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/resenia") // URL base
@RequiredArgsConstructor
public class ReseniaController {

    private final ReseniaService reseniaService;

    // 1️⃣ Crear una reseña
    @PostMapping
    public ResponseEntity<?> crearResenia(@RequestBody CrearReseniaRequest request, Principal principal) {
        try {
            reseniaService.crearResenia(request, principal.getName());
            return ResponseEntity.ok("Resenia creada correctamente");
        } catch (IntercambioInvalidoException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    // 2️⃣ Reseñas recibidas por un usuario
    @GetMapping("/usuario/recibidas")
    public ResponseEntity<List<ReseniaResponse>> getReseniasRecibidas(Principal principal) {
        return ResponseEntity.ok(reseniaService.getReseniasRecibidas(principal.getName()));
    }

    // 3️⃣ Reseñas hechas por un usuario
    @GetMapping("/usuario/hechas")
    public ResponseEntity<List<ReseniaResponse>> getReseniasHechas(Principal principal) {
        return ResponseEntity.ok(reseniaService.getReseniasHechas(principal.getName()));
    }
}
