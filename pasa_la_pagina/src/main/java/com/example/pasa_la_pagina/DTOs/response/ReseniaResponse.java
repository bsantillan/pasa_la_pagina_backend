package com.example.pasa_la_pagina.DTOs.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ReseniaResponse {
    private Long id;
    private String descripcion;
    private Integer valoracion;
    private Long intercambioId;
    private Long autorId;        // no está en la tabla, pero sirve para mostrar quién hizo la reseña
    private String autorNombre;  // no está en la tabla, solo para la UI
}
