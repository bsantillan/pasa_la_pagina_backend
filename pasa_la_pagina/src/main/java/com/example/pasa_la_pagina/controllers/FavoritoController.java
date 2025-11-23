package com.example.pasa_la_pagina.controllers;

import java.security.Principal;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pasa_la_pagina.DTOs.response.PageRecuperarResponse;
import com.example.pasa_la_pagina.exceptions.IntercambioInvalidoException;
import com.example.pasa_la_pagina.services.FavoritoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/favoritos")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:5173" }, allowCredentials = "true")
@Tag(name = "Favoritos", description = "Operaciones sobre publicaciones favoritas")
public class FavoritoController {

    private final FavoritoService favoritoService;

    @Operation(summary = "Agregar una publicacion a la lista de favoritos", description = "Agrega una publicacion a la lista de favoritos de un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mensajes recuperados correctamente"),
            @ApiResponse(responseCode = "404", description = "Chat no encontrado")
    })
    @PostMapping("/nuevo/{publicacionId}")
    public ResponseEntity<?> nuevoFavorito(@PathVariable Long publicacionId, Principal principal) {
        try {
            favoritoService.nuevoFavorito(publicacionId, principal.getName());
            return ResponseEntity.ok("Publicacion agregada a favoritos");   
        } catch (IntercambioInvalidoException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @Operation(summary = "Eliminar una publicacion de la lista de favoritos", description = "Eliminar una publicacion de la lista de favoritos de un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mensajes recuperados correctamente"),
            @ApiResponse(responseCode = "404", description = "Chat no encontrado")
    })
    @DeleteMapping("/eliminar/{publicacionId}")
    public ResponseEntity<?> eliminarFavorito(@PathVariable Long publicacionId, Principal principal) {
        try {
            favoritoService.eliminarFavorito(publicacionId, principal.getName());
            return ResponseEntity.ok("Publicacion eliminada de favoritos");
        } catch (IntercambioInvalidoException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @Operation(summary = "Eliminar una publicacion de la lista de favoritos", description = "Eliminar una publicacion de la lista de favoritos de un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mensajes recuperados correctamente"),
            @ApiResponse(responseCode = "404", description = "Chat no encontrado")
    })
    @GetMapping("/paginado")
    public ResponseEntity<PageRecuperarResponse> recuperarFavoritos(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(favoritoService.recuperarFavoritos(pageable, principal.getName()));
    }
}
