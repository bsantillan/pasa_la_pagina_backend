package com.example.pasa_la_pagina.controllers;

import com.example.pasa_la_pagina.DTOs.requests.MaterialRequest;
import com.example.pasa_la_pagina.DTOs.response.MaterialResponse;
import com.example.pasa_la_pagina.services.MaterialService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/materiales")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @GetMapping
    public ResponseEntity<List<MaterialResponse>> getAll() {
        return ResponseEntity.ok(materialService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(materialService.getById(id));
    }

    @GetMapping("/usuario/{usuarioId}")
        public ResponseEntity<List<MaterialResponse>> getByUsuarioId(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(materialService.getByUsuarioId(usuarioId));
    }

    @PostMapping
    public ResponseEntity<MaterialResponse> create(@RequestBody MaterialRequest request) {
        return ResponseEntity.ok(materialService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterialResponse> update(@PathVariable Long id, @RequestBody MaterialRequest request) {
        return ResponseEntity.ok(materialService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        materialService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
