package com.example.pasa_la_pagina.exceptions;

public class TerminosNoAceptadosException extends RuntimeException {
    public TerminosNoAceptadosException() {
        super("Para registrarte debes aceptar los términos y condiciones");
    }
}
