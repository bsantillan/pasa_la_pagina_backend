package com.example.pasa_la_pagina.DTOs.requests;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String nombre;
    private String apellido;
    private String password;
}
