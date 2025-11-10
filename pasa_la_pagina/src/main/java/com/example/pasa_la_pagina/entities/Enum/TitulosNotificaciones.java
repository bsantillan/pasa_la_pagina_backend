package com.example.pasa_la_pagina.entities.Enum;

public enum TitulosNotificaciones {
    NUEVO_MENSAJE("Nuevo mensaje"),
    SOLICITUD_INTERCAMBIO("Solicitud de intercambio"),
    INTERCAMBIO_ACEPTADO("Intercambio aceptado"),
    INTERCAMBIO_RECHAZADO("Intercambio rechazado");

    private final String titulo;

    TitulosNotificaciones(String string) {
        this.titulo = "";
    }

    public String getTitulo() {
        return titulo;
    }}
