package com.example.pasa_la_pagina.controllers;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pasa_la_pagina.DTOs.requests.DeleteNotificacionRequest;
import com.example.pasa_la_pagina.services.NotificacionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notificaciones")
@RequiredArgsConstructor
public class NotificacionesController {

    private final NotificacionService notificacionService;

    @DeleteMapping("/eliminar")
    public ResponseEntity<Void> eliminarPublicacionById(@Valid @RequestBody DeleteNotificacionRequest request, Principal principal) {
        notificacionService.eliminarNotificacionById(request, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
