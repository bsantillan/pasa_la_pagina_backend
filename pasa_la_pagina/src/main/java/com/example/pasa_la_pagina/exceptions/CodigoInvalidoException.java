package com.example.pasa_la_pagina.exceptions;

public class CodigoInvalidoException extends RuntimeException {
    public CodigoInvalidoException(String mensaje) {
        super(mensaje);
    }
}