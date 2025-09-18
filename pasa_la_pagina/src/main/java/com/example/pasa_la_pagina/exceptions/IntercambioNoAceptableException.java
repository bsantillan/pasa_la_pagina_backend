package com.example.pasa_la_pagina.exceptions;

import com.example.pasa_la_pagina.entities.Enum.EstadoIntercambio;

public class IntercambioNoAceptableException extends RuntimeException {
    public IntercambioNoAceptableException(String detalle) {
        super(detalle);
    }

    public IntercambioNoAceptableException(EstadoIntercambio estadoActual) {
        super("El intercambio no puede aceptarse porque se encuentra " + estadoActual);
    }
}
