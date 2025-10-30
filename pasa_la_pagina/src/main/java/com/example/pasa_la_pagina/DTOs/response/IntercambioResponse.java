package com.example.pasa_la_pagina.DTOs.response;

import java.time.LocalDateTime;

import com.example.pasa_la_pagina.entities.Enum.EstadoIntercambio;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IntercambioResponse {
    private final Long id;
    private String usuario_nombre;
    private String usuario_apellido;
    private final Long propietarioId;
    private final Long chatId;
    private final LocalDateTime fechaInicio;
    private final LocalDateTime fechaFin;
    private final EstadoIntercambio estado;
    private final boolean chatActivo;
}
