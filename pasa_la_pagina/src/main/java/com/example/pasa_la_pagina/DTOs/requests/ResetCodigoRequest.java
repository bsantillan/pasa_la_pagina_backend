package com.example.pasa_la_pagina.DTOs.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetCodigoRequest {
    @NotBlank(message = "El email es obligatorio")
    private String email;
}
