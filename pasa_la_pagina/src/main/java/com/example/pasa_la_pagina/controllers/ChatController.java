package com.example.pasa_la_pagina.controllers;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pasa_la_pagina.DTOs.chat.ChatMessageRequest;
import com.example.pasa_la_pagina.DTOs.chat.ChatMessageResponse;
import com.example.pasa_la_pagina.services.ChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:5173"}, allowCredentials = "true")
@Tag(name = "Chats", description = "Endpoints para administrar chats y mensajes en tiempo real.")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @Operation(
            summary = "Obtener historial de mensajes",
            description = "Recupera los mensajes paginados de un chat identificado por su ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mensajes recuperados correctamente"),
            @ApiResponse(responseCode = "404", description = "Chat no encontrado")
    })
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<Page<ChatMessageResponse>> getMessages(
        @PathVariable Long chatId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(chatService.getMessages(chatId, page, size));
    }

    @Operation(
            summary = "Enviar mensaje",
            description = "Persiste un mensaje y lo difunde a los clientes suscritos via WebSocket."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mensaje enviado"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "404", description = "Chat no encontrado")
    })
    @PostMapping("/{chatId}/send")
    public ResponseEntity<ChatMessageResponse> sendViaRest(
        @PathVariable Long chatId,
        @RequestBody ChatMessageRequest payload,
        @Parameter(hidden = true) Principal principal
    ) {
        ChatMessageResponse resp = chatService.saveMessage(chatId, principal.getName(), payload.getContent());
        messagingTemplate.convertAndSend("/topic/chats/" + chatId, resp);
        return ResponseEntity.ok(resp);
    }
}
