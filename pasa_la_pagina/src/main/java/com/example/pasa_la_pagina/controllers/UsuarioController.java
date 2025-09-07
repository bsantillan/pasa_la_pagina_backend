package com.example.pasa_la_pagina.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pasa_la_pagina.DTOs.response.RecuperarUsuarioResponse;
import com.example.pasa_la_pagina.services.UsuarioService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;;

    @GetMapping("/{id}")
    public ResponseEntity<RecuperarUsuarioResponse> recuperarUsuarioById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.recuperarUsuario(id));
    }
    
}
