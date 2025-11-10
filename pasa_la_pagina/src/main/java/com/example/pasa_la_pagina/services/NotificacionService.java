package com.example.pasa_la_pagina.services;

import java.time.LocalDateTime;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.pasa_la_pagina.DTOs.response.NotificacionResponse;
import com.example.pasa_la_pagina.entities.Enum.TitulosNotificaciones;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final SimpMessagingTemplate messagingTemplate;

    public void enviarNotificacionAUsuario(TitulosNotificaciones titulo, String nombreUsuario, String apellidoUsuario, String mensaje, Long receptorId) {
        NotificacionResponse notificacion = new NotificacionResponse(
                titulo,
                nombreUsuario,
                apellidoUsuario,
                mensaje,
                LocalDateTime.now()
        );

        // canal único por usuario
        messagingTemplate.convertAndSend("/topic/notificaciones/" + receptorId, notificacion);
    }
}

