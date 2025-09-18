package com.example.pasa_la_pagina.controllers;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pasa_la_pagina.DTOs.requests.SolicitarIntercambioRequest;
import com.example.pasa_la_pagina.DTOs.response.IntercambioResponse;
import com.example.pasa_la_pagina.services.IntercambioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/intercambios")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:5173"}, allowCredentials = "true")
@Tag(name = "Intercambios", description = "Operaciones sobre propuestas de intercambio")
public class IntercambioController {

    private final IntercambioService intercambioService;

    @PostMapping
    @Operation(summary = "Solicitar intercambio", description = "Registra una propuesta de intercambio. El chat queda deshabilitado hasta la aceptacion del propietario.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Intercambio creado"),
        @ApiResponse(responseCode = "400", description = "Solicitud invalida"),
        @ApiResponse(responseCode = "404", description = "Publicacion o usuario inexistente")
    })
    public ResponseEntity<IntercambioResponse> solicitar(
        @Valid @RequestBody SolicitarIntercambioRequest request,
        Principal principal
    ) {
        IntercambioResponse response = intercambioService.solicitarIntercambio(request, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{intercambioId}/aceptar")
    @Operation(summary = "Aceptar intercambio", description = "Solo el propietario habilita el chat cuando acepta la propuesta.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Intercambio habilitado"),
        @ApiResponse(responseCode = "403", description = "Usuario no autorizado a aceptar"),
        @ApiResponse(responseCode = "404", description = "Intercambio inexistente"),
        @ApiResponse(responseCode = "409", description = "Intercambio no se puede aceptar")
    })
    public ResponseEntity<IntercambioResponse> aceptar(
        @PathVariable Long intercambioId,
        Principal principal
    ) {
        IntercambioResponse response = intercambioService.aceptarIntercambio(intercambioId, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{intercambioId}/cancelar")
    @Operation(summary = "Cancelar intercambio", description = "Cancela una propuesta pendiente e inhabilita el chat asociado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Intercambio cancelado"),
        @ApiResponse(responseCode = "403", description = "Usuario no autorizado a cancelar"),
        @ApiResponse(responseCode = "404", description = "Intercambio inexistente"),
        @ApiResponse(responseCode = "409", description = "Intercambio no se puede cancelar")
    })
    public ResponseEntity<IntercambioResponse> cancelar(
        @PathVariable Long intercambioId,
        Principal principal
    ) {
        IntercambioResponse response = intercambioService.cancelarIntercambio(intercambioId, principal.getName());
        return ResponseEntity.ok(response);
    }
}
