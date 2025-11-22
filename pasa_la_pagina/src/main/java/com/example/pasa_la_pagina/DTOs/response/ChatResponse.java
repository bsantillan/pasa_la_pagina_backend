package com.example.pasa_la_pagina.DTOs.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
    private Long id;
    private String titulo_publicacion;
    private Long intercambio_id;
    private String usuario;
    private String usuario_email;
}
