package com.example.pasa_la_pagina.exceptions;

public class ChatDeshabilitadoException extends RuntimeException {
    public ChatDeshabilitadoException(Long chatId) {
        super("El chat " + chatId + " se encuentra deshabilitado");
    }
}
