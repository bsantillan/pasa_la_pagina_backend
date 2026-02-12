package com.example.pasa_la_pagina.DTOs.requests;

import com.example.pasa_la_pagina.entities.Enum.TokenType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResendCodigoRequest {
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotNull(message = "El tipo de codigo es obligatorio")
    private TokenType tipo_codigo;
}
