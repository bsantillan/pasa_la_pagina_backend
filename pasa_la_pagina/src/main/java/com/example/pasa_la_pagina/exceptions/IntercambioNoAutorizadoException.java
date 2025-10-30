package com.example.pasa_la_pagina.exceptions;

public class IntercambioNoAutorizadoException extends RuntimeException {
    public IntercambioNoAutorizadoException() {
        super("No tenes permisos para operar sobre este intercambio");
    }
}
