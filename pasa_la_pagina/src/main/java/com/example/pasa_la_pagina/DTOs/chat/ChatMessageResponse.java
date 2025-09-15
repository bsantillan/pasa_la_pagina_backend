package com.example.pasa_la_pagina.DTOs.chat;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessageResponse {
    private Long id;
    private Long chatId;
    private String content;
    private String senderEmail;
    private LocalDateTime createdAt;
}

