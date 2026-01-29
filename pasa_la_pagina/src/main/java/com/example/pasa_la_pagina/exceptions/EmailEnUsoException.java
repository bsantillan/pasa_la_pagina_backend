package com.example.pasa_la_pagina.exceptions;

public class EmailEnUsoException extends RuntimeException {
    public EmailEnUsoException() {
        super("El email ya está registrado");
    }
}