package com.example.pasa_la_pagina.controllers;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pasa_la_pagina.DTOs.requests.BuscarIntercambioRequest;
import com.example.pasa_la_pagina.DTOs.response.IntercambioEstadoEvento;
import com.example.pasa_la_pagina.DTOs.response.PageRecuperarResponse;
import com.example.pasa_la_pagina.entities.Intercambio;
import com.example.pasa_la_pagina.entities.Enum.EstadoIntercambio;
import com.example.pasa_la_pagina.exceptions.IntercambioInvalidoException;
import com.example.pasa_la_pagina.exceptions.IntercambioNoEncontradoException;
import com.example.pasa_la_pagina.repositories.IntercambioRepository;
import com.example.pasa_la_pagina.services.IntercambioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/intercambio")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:5173" }, allowCredentials = "true")
@Tag(name = "Intercambios", description = "Operaciones sobre propuestas de intercambio")
public class IntercambioController {

    private final IntercambioService intercambioService;
    private final IntercambioRepository intercambioRepository;
    private final SimpMessagingTemplate webSocketTemplate;


    @PostMapping("/{id}/generar-codigo")
    @Operation(summary = "Generar código de validación", description = "Genera un código de validación que expira en 1 hora")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Código generado"),
            @ApiResponse(responseCode = "403", description = "Usuario no autorizado"),
            @ApiResponse(responseCode = "404", description = "Intercambio no encontrado"),
            @ApiResponse(responseCode = "409", description = "No se puede generar código para este intercambio")
    })
    public ResponseEntity<String> generarCodigoValidacion(
            @PathVariable Long id, 
            Principal principal) {
        try {
            String codigo = intercambioService.generarCodigoValidacion(id, principal.getName());
            return ResponseEntity.ok(codigo);
        } catch (IntercambioInvalidoException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/validar-con-codigo")
    @Operation(summary = "Validar intercambio con código", description = "Valida el intercambio usando el código proporcionado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Intercambio validado"),
            @ApiResponse(responseCode = "403", description = "Usuario no autorizado"),
            @ApiResponse(responseCode = "404", description = "Intercambio no encontrado"),
            @ApiResponse(responseCode = "409", description = "Código inválido o expirado")
    })
    public ResponseEntity<?> validarIntercambioConCodigo(
            @PathVariable Long id,
            @RequestParam String codigo,
            Principal principal) {
        try {
            intercambioService.validarIntercambioConCodigo(id, codigo, principal.getName());
            return ResponseEntity.ok("Intercambio validado correctamente");
        } catch (IntercambioInvalidoException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/estado-actualizado")
    public ResponseEntity<IntercambioEstadoEvento> getEstadoActualizado(
            @PathVariable Long id,
            Principal principal) {
        
        Intercambio intercambio = intercambioRepository.findById(id).orElseThrow(() -> new IntercambioNoEncontradoException(id));

        // Verificar si el estado ha cambiado por expiración
        if (intercambio.getEstado() == EstadoIntercambio.ACEPTADO && 
        intercambio.debeCambiarARechazado()) {
        
        // Actualizar estado en la base de datos
        intercambio.setEstado(EstadoIntercambio.RECHAZADO);
        intercambio.setFechaFin(LocalDateTime.now());
        intercambio.setCodigoValidacion(null);
        intercambio.setCodigoExpiracion(null);
        
        intercambioRepository.save(intercambio);
        
        // Enviar notificación WebSocket
        IntercambioEstadoEvento evento = IntercambioEstadoEvento.builder()
                .intercambioId(intercambio.getId())
                .estado(EstadoIntercambio.RECHAZADO)
                .puedeDejarResena(true)
                .mensaje("Intercambio rechazado por expiración del código de validación")
                .build();

        webSocketTemplate.convertAndSend(
            "/topic/intercambio/" + intercambio.getId() + "/estado", 
            evento
        );
    }

    IntercambioEstadoEvento evento = IntercambioEstadoEvento.builder()
            .intercambioId(intercambio.getId())
            .estado(intercambio.getEstado())
            .puedeDejarResena(intercambio.getEstado() == EstadoIntercambio.CONCRETADO || 
                             intercambio.getEstado() == EstadoIntercambio.RECHAZADO)
            .mensaje(getMensajeEstado(intercambio.getEstado()))
            .build();

    return ResponseEntity.ok(evento);
}

    private String getMensajeEstado(EstadoIntercambio estado) {
        switch (estado) {
            case CONCRETADO:
                return "Intercambio concretado exitosamente";
            case RECHAZADO:
                return "Intercambio rechazado por expiración del código de validación";
            default:
                return "Estado actual del intercambio";
        }
    }

    @PostMapping("/solicitar/{id}")
    @Operation(summary = "Solicitar intercambio", description = "Registra una propuesta de intercambio. El chat queda deshabilitado hasta la aceptacion del propietario.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Intercambio creado"),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida"),
            @ApiResponse(responseCode = "404", description = "Publicacion o usuario inexistente")
    })
    public ResponseEntity<?> solicitar(@PathVariable Long id, Principal principal) {
        try {
            System.out.println(principal.getName());
            intercambioService.solicitarIntercambio(id, principal.getName());
            return ResponseEntity.ok("Intercambio solicitado correctamente.");
        } catch (IntercambioInvalidoException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @PatchMapping("/aceptar/{id}")
    @Operation(summary = "Aceptar intercambio", description = "Acepta una propuesta pendiente de un intercambio. Solo el propietario habilita el chat cuando acepta la propuesta.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Intercambio habilitado"),
            @ApiResponse(responseCode = "403", description = "Usuario no autorizado a aceptar"),
            @ApiResponse(responseCode = "404", description = "Intercambio inexistente"),
            @ApiResponse(responseCode = "409", description = "Intercambio no se puede aceptar")
    })
    public ResponseEntity<?> aceptar(@PathVariable Long id, Principal principal) {
        try {
            intercambioService.aceptarIntercambio(id, principal.getName());
            return ResponseEntity.ok("Intercambio aceptado correctamente.");
        } catch (IntercambioInvalidoException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
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
        try {
            intercambioService.concretarIntercambio(id, principal.getName());
            return ResponseEntity.ok("Intercambio concretado correctamente.");
        } catch (IntercambioInvalidoException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @PatchMapping("/cancelar/{id}")
    @Operation(summary = "Cancelar intercambio", description = "Cancela una propuesta aceptada de un intercambio")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Intercambio cancelado"),
            @ApiResponse(responseCode = "403", description = "Usuario no autorizado a cancelar"),
            @ApiResponse(responseCode = "404", description = "Intercambio inexistente"),
            @ApiResponse(responseCode = "409", description = "Intercambio no se puede cancelar")
    })
    public ResponseEntity<?> cancelar(@PathVariable Long id, Principal principal) {
        try {
            intercambioService.cancelarIntercambio(id, principal.getName());
            return ResponseEntity.ok("Intercambio cancelado correctamente.");
        } catch (IntercambioInvalidoException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @PatchMapping("/rechazar/{id}")
    @Operation(summary = "Rechazar intercambio", description = "Rechaza una propuesta pendiente de un intercambio")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Intercambio rechazado"),
            @ApiResponse(responseCode = "403", description = "Usuario no autorizado a cancelar"),
            @ApiResponse(responseCode = "404", description = "Intercambio inexistente"),
            @ApiResponse(responseCode = "409", description = "Intercambio no se puede rechazar")
    })
    public ResponseEntity<?> rechazar(@PathVariable Long id, Principal principal) {
        try {
            intercambioService.rechazarIntercambio(id, principal.getName());
            return ResponseEntity.ok("Intercambio rechazado correctamente.");
        } catch (IntercambioInvalidoException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @PostMapping("/paginado")
    @Operation(summary = "Buscar intercambios", description = "Permite buscar y filtrar intercambios según varios criterios: rol del usuario, estado del intercambio, fecha de inicio, título de la publicación y nombre del usuario. También se puede ordenar por fecha, título o estado.")
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
        System.out.println(filtros);

        return ResponseEntity.ok(intercambioService.buscarIntercambios(page, size, principal.getName(), filtros));
    }
}
