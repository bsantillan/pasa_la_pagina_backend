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
public class IntercambioEstadoEvento {
    private Long intercambioId;
    private EstadoIntercambio estado;
    private Boolean puedeDejarResena;
    private String mensaje;
}