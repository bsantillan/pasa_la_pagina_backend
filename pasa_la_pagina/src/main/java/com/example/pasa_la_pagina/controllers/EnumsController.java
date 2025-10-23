package com.example.pasa_la_pagina.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pasa_la_pagina.DTOs.response.IdiomaResponse;
import com.example.pasa_la_pagina.entities.Enum.NivelEducativo;
import com.example.pasa_la_pagina.entities.Enum.TipoMaterial;
import com.example.pasa_la_pagina.entities.Enum.TipoOferta;
import com.example.pasa_la_pagina.services.IdiomaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/enums")
@RequiredArgsConstructor
public class EnumsController {

    private final IdiomaService idiomaService;

    @GetMapping("/idiomas/buscar")
    public ResponseEntity<List<IdiomaResponse>> buscarIdiomas(@RequestParam String q) {
        return ResponseEntity.ok(idiomaService.buscarIdiomas(q));
    }

    @GetMapping("/niveles-educativos")
    public ResponseEntity<List<String>> nivelesEducativos() {
        List<String> niveles = Arrays.stream(NivelEducativo.values())
                .map(Enum::name)
                .toList();
        return ResponseEntity.ok(niveles);
    }

    @GetMapping("/tipo-material")
    public ResponseEntity<List<String>> tiposMateriales() {
        List<String> tipos = Arrays.stream(TipoMaterial.values())
                .map(Enum::name)
                .toList();
        return ResponseEntity.ok(tipos);
    }

    @GetMapping("/tipo-oferta")
    public ResponseEntity<List<String>> tiposOferta() {
        List<String> tipos = Arrays.stream(TipoOferta.values())
                .map(Enum::name)
                .toList();
        return ResponseEntity.ok(tipos);
    }
}
