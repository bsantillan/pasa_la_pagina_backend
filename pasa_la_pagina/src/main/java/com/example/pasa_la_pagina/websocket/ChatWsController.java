package com.example.pasa_la_pagina.websocket;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.example.pasa_la_pagina.DTOs.requests.ChatMessage;
import com.example.pasa_la_pagina.DTOs.response.MensajeResponse;
import com.example.pasa_la_pagina.services.ChatService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatWsController {

    private final ChatService chatService;

    // Recibir mensaje del frontend
    @MessageMapping("/chat.sendMessage/{chatId}") // /app/chat.sendMessage/{chatId}
    @SendTo("/topic/chat/{chatId}")
    public void sendMessage(@DestinationVariable Long chatId, @Payload ChatMessage chatMessage) {
        // Guardar mensaje en BD
        MensajeResponse mensajeGuardado = chatService.guardarMensaje(chatMessage, chatMessage.getSender());

        // Enviar a los suscriptores del chat específico
        chatService.sendToSubscribers(chatId, mensajeGuardado);
    }
}
