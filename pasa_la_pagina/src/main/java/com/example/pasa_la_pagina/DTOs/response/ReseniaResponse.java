package com.example.pasa_la_pagina.DTOs.response;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ReseniaResponse {
    private Long id;
    private LocalDateTime fecha;
    private String descripcion;
    private Integer valoracion;
    private Long intercambioId;
    private Long usuarioId;        
    private String usuarioNombre;  // no está en la tabla, solo para la UI
}
