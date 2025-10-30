package com.example.pasa_la_pagina.DTOs.requests;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private Long chatId;      // ID del chat
    private String sender;    // Nombre del usuario que envía
    private String content;   // Contenido del mensaje
}



