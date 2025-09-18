package com.example.pasa_la_pagina.exceptions;

public class IntercambioNoEncontradoException extends RuntimeException {
    public IntercambioNoEncontradoException(Long intercambioId) {
        super("No se encontro el intercambio " + intercambioId);
    }
}
