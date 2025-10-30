package com.example.pasa_la_pagina.exceptions;

public class PublicacionNoEncontradaException extends RuntimeException {
    public PublicacionNoEncontradaException(Long publicacionId) {
        super("No se encontro la publicacion " + publicacionId);
    }
}
