package com.example.pasa_la_pagina.DTOs.requests;

import java.time.LocalDateTime;
import java.util.List;

import com.example.pasa_la_pagina.entities.Enum.EstadoIntercambio;
import com.example.pasa_la_pagina.entities.Enum.IntercambioOrden;

import lombok.Data;

@Data
public class BuscarIntercambioRequest {
    private List<String> rolesUsuario;
    private List<EstadoIntercambio> estadosIntercambio;
    private LocalDateTime fechaInicio;
    private String tituloPublicaicon;
    private String usuario;

    private IntercambioOrden ordenarPor; 
    private boolean ordenDescendente; 
}
