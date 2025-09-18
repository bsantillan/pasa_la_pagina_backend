package com.example.pasa_la_pagina.DTOs.requests;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class SolicitarIntercambioRequest {

    @NotNull(message = "El publicacionId es obligatorio")
    private Long publicacionId;
}
