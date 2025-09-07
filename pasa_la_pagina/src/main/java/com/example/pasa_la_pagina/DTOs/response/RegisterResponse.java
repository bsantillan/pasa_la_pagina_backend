package com.example.pasa_la_pagina.DTOs.response;

import lombok.Data;

@Data
public class RegisterResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String accessToken;
    private String refreshToken;
}
