package com.example.pasa_la_pagina.DTOs.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MensajeResponse {
    private String contenido;
    private Long usuarioId;
    private String usuario;
    private LocalDateTime fechaInicio;
}

