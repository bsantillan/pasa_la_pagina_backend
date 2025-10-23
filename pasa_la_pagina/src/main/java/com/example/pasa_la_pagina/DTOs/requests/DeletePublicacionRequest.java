package com.example.pasa_la_pagina.DTOs.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeletePublicacionRequest {
    @NotNull(message = "El id de la publicacion es obligatorio")
    private Long id;
    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;
}
