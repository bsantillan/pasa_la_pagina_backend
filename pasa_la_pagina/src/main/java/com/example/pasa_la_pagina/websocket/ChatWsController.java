package com.example.pasa_la_pagina.websocket;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.example.pasa_la_pagina.DTOs.requests.MensajeRequest;
import com.example.pasa_la_pagina.services.ChatService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatWsController {

    private final ChatService chatService;
    // { changed code } - eliminamos SimpMessagingTemplate del controlador; ahora lo
    // hace el servicio

    /**
     * Handler STOMP para recibir mensajes entrantes desde clientes WebSocket.
     *
     * Uso en cliente:
     * stompClient.send('/app/chats/send/{chatId}', {}, JSON.stringify({ content:
     * 'hola' }));
     *
     * Nota: Principal.getName() debe devolver el identificador que usamos en
     * convertAndSendToUser
     * (en este proyecto usamos el email como nombre de usuario).
     */
    @MessageMapping("/chats/send/{chatId}")
    public void sendMessage(@DestinationVariable Long chatId, MensajeRequest payload, Principal principal) {
        String senderEmail = principal.getName();
        // El servicio guarda el mensaje y se encarga de notificar a los participantes
        chatService.saveMessage(chatId, senderEmail, payload.getContent());
    }
}