package com.example.pasa_la_pagina.exceptions;

public class CuentaVerificadaException extends RuntimeException {
    public CuentaVerificadaException() {
        super("Cuenta ya verificada");
    }
}