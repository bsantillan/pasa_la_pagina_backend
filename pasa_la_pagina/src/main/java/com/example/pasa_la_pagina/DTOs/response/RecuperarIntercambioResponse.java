package com.example.pasa_la_pagina.DTOs.response;

import java.time.LocalDateTime;

import com.example.pasa_la_pagina.entities.Enum.EstadoIntercambio;

import lombok.Data;

@Data
public class RecuperarIntercambioResponse {
    private Long id;
    private Boolean solicitanteConcreto;
    private Boolean propietarioConcreto;
    private String rolUsuario;
    private EstadoIntercambio estadoIntercambio;
    private LocalDateTime fechaInicio;
    private String tituloPublicaicon;
    private String usuario;
    private String usuarioEmail;
    private Long chatId;
}
