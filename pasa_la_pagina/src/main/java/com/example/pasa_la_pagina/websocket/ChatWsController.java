package com.example.pasa_la_pagina.websocket;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.pasa_la_pagina.DTOs.chat.ChatMessageRequest;
import com.example.pasa_la_pagina.DTOs.chat.ChatMessageResponse;
import com.example.pasa_la_pagina.services.ChatService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatWsController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chats/{chatId}/send")
    public void sendMessage(@DestinationVariable Long chatId, ChatMessageRequest payload, Principal principal) {
        String senderEmail = principal.getName();
        ChatMessageResponse resp = chatService.saveMessage(chatId, senderEmail, payload.getContent());
        messagingTemplate.convertAndSend("/topic/chats/" + chatId, resp);
    }
}

