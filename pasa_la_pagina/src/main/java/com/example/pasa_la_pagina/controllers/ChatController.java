package com.example.pasa_la_pagina.controllers;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import com.example.pasa_la_pagina.DTOs.chat.ChatMessageRequest;
import com.example.pasa_la_pagina.DTOs.chat.ChatMessageResponse;
import com.example.pasa_la_pagina.entities.Chat;
import com.example.pasa_la_pagina.services.ChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:5173"}, allowCredentials = "true")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<Page<ChatMessageResponse>> getMessages(
        @PathVariable Long chatId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(chatService.getMessages(chatId, page, size));
    }

    @PostMapping
    public ResponseEntity<Chat> createChat(@RequestParam String title) {
        return ResponseEntity.ok(chatService.createChat(title));
    }

    // Puente REST para enviar y difundir mensajes a STOMP
    @PostMapping("/{chatId}/send")
    public ResponseEntity<ChatMessageResponse> sendViaRest(
        @PathVariable Long chatId,
        @RequestBody ChatMessageRequest payload,
        Principal principal
    ) {
        ChatMessageResponse resp = chatService.saveMessage(chatId, principal.getName(), payload.getContent());
        messagingTemplate.convertAndSend("/topic/chats/" + chatId, resp);
        return ResponseEntity.ok(resp);
    }
}
