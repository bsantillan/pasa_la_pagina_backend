package com.example.pasa_la_pagina.DTOs.response;

import java.time.LocalDateTime;

import com.example.pasa_la_pagina.entities.Enum.TitulosNotificaciones;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionResponse {
    private TitulosNotificaciones titulo;          
    private String mensaje;       
    private LocalDateTime fecha;
}

