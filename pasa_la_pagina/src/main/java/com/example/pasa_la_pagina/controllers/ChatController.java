package com.example.pasa_la_pagina.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pasa_la_pagina.DTOs.response.MensajeResponse;
import com.example.pasa_la_pagina.services.ChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:5173" }, allowCredentials = "true")
@Tag(name = "Chats", description = "Endpoints para administrar chats y mensajes en tiempo real.")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "Obtener historial de mensajes", description = "Recupera los mensajes paginados de un chat identificado por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mensajes recuperados correctamente"),
            @ApiResponse(responseCode = "404", description = "Chat no encontrado")
    })
    @GetMapping("/chat/{chatId}/mensajes")
    public ResponseEntity<List<MensajeResponse>> getMensajes(@PathVariable Long chatId) {
        return ResponseEntity.ok(chatService.obtenerMensajes(chatId));
    }

}
