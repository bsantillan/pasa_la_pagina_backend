package com.example.pasa_la_pagina.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pasa_la_pagina.DTOs.requests.UpdateUsuarioRequest;
import com.example.pasa_la_pagina.DTOs.response.RecuperarUsuarioResponse;
import com.example.pasa_la_pagina.services.UsuarioService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;;

    @GetMapping("/{id}")
    public ResponseEntity<RecuperarUsuarioResponse> recuperarUsuarioById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.recuperarUsuario(id));
    }

    @GetMapping("/")
    public ResponseEntity<List<RecuperarUsuarioResponse>> recurarUsuarios() {
        return ResponseEntity.ok(usuarioService.recuperarTodosUsuarios());
    }

    @GetMapping("/email")
    public ResponseEntity<RecuperarUsuarioResponse> recuperarUsuarioByEmail(@RequestParam String email) {
        return ResponseEntity.ok(usuarioService.recuperarUsuarioByEmail(email));
    }
    
    @PutMapping("update/{id}")
    public ResponseEntity<RecuperarUsuarioResponse> actualizarUsuario(
            @PathVariable Long id,
            @RequestBody UpdateUsuarioRequest request) {

        RecuperarUsuarioResponse updated = usuarioService.actualizarUsuario(id, request);
        return ResponseEntity.ok(updated);
    }
    
    
}
