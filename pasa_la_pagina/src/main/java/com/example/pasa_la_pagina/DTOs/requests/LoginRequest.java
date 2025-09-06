package com.example.pasa_la_pagina.DTOs.requests;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
