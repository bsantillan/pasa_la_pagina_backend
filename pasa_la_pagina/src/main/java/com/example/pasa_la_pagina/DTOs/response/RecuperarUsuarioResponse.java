package com.example.pasa_la_pagina.DTOs.response;

import lombok.Data;

@Data
public class RecuperarUsuarioResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
}
