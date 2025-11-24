package com.example.pasa_la_pagina.DTOs.response;

import com.example.pasa_la_pagina.entities.Enum.EstadoIntercambio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntercambioResenaResponse {
    private Long id;
    private String tituloPublicacion;
    private String nombre;
    private String apellido;
    private Long usuarioId;
    private String usuarioEmail;
    private EstadoIntercambio estado;
    private String fechaInicio;
    private Boolean esReseñable;
    private String rolUsuario; // "Solicitante" o "Propietario"
}
