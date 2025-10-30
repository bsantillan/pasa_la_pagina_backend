package com.example.pasa_la_pagina.DTOs.requests;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class CrearReseniaRequest {

    @NotBlank
    private String descripcion;

    @NotNull
    private Integer valoracion;

    @NotNull
    private Long intercambioId;  // Para saber a qué intercambio pertenece la reseña

    @NotNull
    private Long autorId;        // Para saber quién crea la reseña
}
