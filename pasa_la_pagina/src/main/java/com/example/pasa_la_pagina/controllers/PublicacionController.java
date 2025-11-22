package com.example.pasa_la_pagina.controllers;

import java.security.Principal;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pasa_la_pagina.DTOs.requests.BuscarPublicacionRequest;
import com.example.pasa_la_pagina.DTOs.requests.DeletePublicacionRequest;
import com.example.pasa_la_pagina.DTOs.requests.PublicacionApunteRequest;
import com.example.pasa_la_pagina.DTOs.requests.PublicacionLibroRequest;
import com.example.pasa_la_pagina.DTOs.requests.UpdatePublicacionRequest;
import com.example.pasa_la_pagina.DTOs.response.PageRecuperarResponse;
import com.example.pasa_la_pagina.DTOs.response.RecuperarPublicacionResponse;
import com.example.pasa_la_pagina.services.PublicacionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/publicacion")
@RequiredArgsConstructor
public class PublicacionController {

    private final PublicacionService publicacionService;

    @PostMapping("/nuevo/libro")
    public ResponseEntity<RecuperarPublicacionResponse> nuevaPublicacionLibro(
            @Valid @RequestBody PublicacionLibroRequest request) {
        return ResponseEntity.ok(publicacionService.nuevaPublicacionLibro(request));
    }

    @PostMapping("/nuevo/apunte")
    public ResponseEntity<RecuperarPublicacionResponse> nuevaPublicacionApunte(
            @Valid @RequestBody PublicacionApunteRequest request) {
        return ResponseEntity.ok(publicacionService.nuevaPublicacionApunte(request));
    }

    @PutMapping("/actualizar")
    public ResponseEntity<RecuperarPublicacionResponse> actualizarPublicacion(
            @Valid @RequestBody UpdatePublicacionRequest request) {
        return ResponseEntity.ok(publicacionService.actualizarPublicacion(request));
    }

    @PostMapping("/buscar")
    public ResponseEntity<PageRecuperarResponse> buscarPublicaciones(
            @Valid @RequestBody(required = false) BuscarPublicacionRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(publicacionService.buscarPublicaciones(request, pageable, principal.getName()));
    }

    @GetMapping("/paginado")
    public ResponseEntity<PageRecuperarResponse> recuperarPublicaciones(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam Double usuario_latitud,
            @RequestParam Double usuario_longitud,
            Principal principal) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(publicacionService.recuperarPublicaciones(pageable,usuario_latitud,usuario_longitud,principal.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecuperarPublicacionResponse> recuperarPublicacionById(@PathVariable Long id) {
        return ResponseEntity.ok(publicacionService.recuperarPublicacionById(id));
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<Void> eliminarPublicacionById(@Valid @RequestBody DeletePublicacionRequest request, Principal principal) {
        publicacionService.eliminarPublicacionById(request, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{usuario_id}")
    public ResponseEntity<PageRecuperarResponse> recuperarPublicacionesByUserId(
            @PathVariable Long usuario_id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(publicacionService.recuperarPublicacionesByUserId(usuario_id,pageable));
    }
}
