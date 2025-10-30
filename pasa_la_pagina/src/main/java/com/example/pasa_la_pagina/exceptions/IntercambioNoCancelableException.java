package com.example.pasa_la_pagina.exceptions;

import com.example.pasa_la_pagina.entities.Enum.EstadoIntercambio;

public class IntercambioNoCancelableException extends RuntimeException {
    public IntercambioNoCancelableException(EstadoIntercambio estadoActual) {
        super("El intercambio no puede cancelarse porque se encuentra " + estadoActual);
    }
}
