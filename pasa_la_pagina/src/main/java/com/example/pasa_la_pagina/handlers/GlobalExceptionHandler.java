package com.example.pasa_la_pagina.handlers;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.pasa_la_pagina.exceptions.CredencialesInvalidasException;
import com.example.pasa_la_pagina.exceptions.EmailEnUsoException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailEnUsoException.class)
    public ResponseEntity<Map<String, String>> handleEmailEnUso(EmailEnUsoException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "timestamp", Instant.now().toString(),
                        "status", String.valueOf(HttpStatus.CONFLICT.value()),
                        "error", ex.getMessage()));
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, String>> handleCredencialesInvalidas(CredencialesInvalidasException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "timestamp", Instant.now().toString(),
                        "status", String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                        "error", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleOtrasExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Ocurrió un error inesperado"));
    }

}
