package com.example.pasa_la_pagina.DTOs.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerificarEmailRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String codigo;
}
