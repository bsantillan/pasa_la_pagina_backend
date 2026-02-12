package com.example.pasa_la_pagina.exceptions;

public class EmailNoVerificadoException extends RuntimeException {
    public EmailNoVerificadoException(String mensaje) {
        super(mensaje);
    }
}

