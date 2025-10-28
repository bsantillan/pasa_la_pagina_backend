package com.example.pasa_la_pagina.controllers;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pasa_la_pagina.DTOs.requests.BuscarIntercambioRequest;
import com.example.pasa_la_pagina.DTOs.response.PageRecuperarResponse;
import com.example.pasa_la_pagina.services.IntercambioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/intercambio")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:5173" }, allowCredentials = "true")
@Tag(name = "Intercambios", description = "Operaciones sobre propuestas de intercambio")
public class IntercambioController {

    private final IntercambioService intercambioService;

    @PostMapping("/solicitar/{id}")
    @Operation(summary = "Solicitar intercambio", description = "Registra una propuesta de intercambio. El chat queda deshabilitado hasta la aceptacion del propietario.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Intercambio creado"),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida"),
            @ApiResponse(responseCode = "404", description = "Publicacion o usuario inexistente")
    })
    public ResponseEntity<?> solicitar(@NotNull Long id, Principal principal) {
        intercambioService.solicitarIntercambio(id, principal.getName());
        return ResponseEntity.ok("Intercambio solicitado correctamente.");
    }

    @PatchMapping("/aceptar/{id}")
    @Operation(summary = "Aceptar intercambio", description = "Solo el propietario habilita el chat cuando acepta la propuesta.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Intercambio habilitado"),
            @ApiResponse(responseCode = "403", description = "Usuario no autorizado a aceptar"),
            @ApiResponse(responseCode = "404", description = "Intercambio inexistente"),
            @ApiResponse(responseCode = "409", description = "Intercambio no se puede aceptar")
    })
    public ResponseEntity<?> aceptar(@PathVariable Long id, Principal principal) {
        intercambioService.aceptarIntercambio(id, principal.getName());
        return ResponseEntity.ok("Intercambio aceptado correctamente.");
    }

    @PatchMapping("/concretar/{id}")
    @Operation(summary = "Concretar intercambio", description = "Ambos usuarios deben concretarlo. Cuando ambos confirman, el estado se actualiza a CONCRETADO.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Intercambio concretado"),
            @ApiResponse(responseCode = "403", description = "Usuario no autorizado a concretar"),
            @ApiResponse(responseCode = "404", description = "Intercambio inexistente"),
            @ApiResponse(responseCode = "409", description = "Intercambio no se puede concretar")
    })
    public ResponseEntity<?> concretar(@PathVariable Long id, Principal principal) {
        intercambioService.concretarIntercambio(id, principal.getName());
        return ResponseEntity.ok("Intercambio concretado correctamente.");
    }

    @PatchMapping("/concretar/{id}")
    @Operation(summary = "Cancelar intercambio", description = "Cancela una propuesta pendiente e cancela un intercambio ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Intercambio cancelado"),
            @ApiResponse(responseCode = "403", description = "Usuario no autorizado a cancelar"),
            @ApiResponse(responseCode = "404", description = "Intercambio inexistente"),
            @ApiResponse(responseCode = "409", description = "Intercambio no se puede cancelar")
    })
    public ResponseEntity<?> cancelar(@PathVariable Long id, Principal principal) {
        intercambioService.cancelarIntercambio(id, principal.getName());
        return ResponseEntity.ok("Intercambio cancelado correctamente.");
    }

    @PostMapping("/paginado")
    @Operation(
        summary = "Buscar intercambios",
        description = "Permite buscar y filtrar intercambios según varios criterios: rol del usuario, estado del intercambio, fecha de inicio, título de la publicación y nombre del usuario. También se puede ordenar por fecha, título o estado."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Se retornan los intercambios que cumplen los filtros"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<PageRecuperarResponse> buscarIntercambios(
            @Valid @RequestBody(required = false) BuscarIntercambioRequest filtros,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal) {
        return ResponseEntity.ok(intercambioService.buscarIntercambios(page, size, principal.getName(), filtros));
    }
}
