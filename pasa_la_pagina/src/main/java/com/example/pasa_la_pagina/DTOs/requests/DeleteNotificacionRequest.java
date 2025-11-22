package com.example.pasa_la_pagina.DTOs.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeleteNotificacionRequest {
    @NotNull(message = "El id de la notificacion es obligatorio")
    private Long id;
}
