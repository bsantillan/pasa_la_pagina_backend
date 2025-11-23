package com.example.pasa_la_pagina.DTOs.response;

import java.time.LocalDateTime;

import com.example.pasa_la_pagina.entities.Enum.TipoNotificacion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecuperarNotificacionResponse {
    private Long id;
    private String titulo;          
    private String mensaje;       
    private LocalDateTime fecha;
    private Long intercambio_id;
    private Long chat_id;
    private Long mensaje_id;
    private String usuario;
    private TipoNotificacion tipo_notificacion;
}

