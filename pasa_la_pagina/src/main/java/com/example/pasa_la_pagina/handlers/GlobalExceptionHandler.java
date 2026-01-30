package com.example.pasa_la_pagina.handlers;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.pasa_la_pagina.exceptions.ChatDeshabilitadoException;
import com.example.pasa_la_pagina.exceptions.CodigoInvalidoException;
import com.example.pasa_la_pagina.exceptions.CredencialesInvalidasException;
import com.example.pasa_la_pagina.exceptions.CuentaVerificadaException;
import com.example.pasa_la_pagina.exceptions.EmailEnUsoException;
import com.example.pasa_la_pagina.exceptions.EmailNoVerificadoException;
import com.example.pasa_la_pagina.exceptions.IntercambioInvalidoException;
import com.example.pasa_la_pagina.exceptions.IntercambioNoAceptableException;
import com.example.pasa_la_pagina.exceptions.IntercambioNoAutorizadoException;
import com.example.pasa_la_pagina.exceptions.IntercambioNoCancelableException;
import com.example.pasa_la_pagina.exceptions.IntercambioNoEncontradoException;
import com.example.pasa_la_pagina.exceptions.PasswordInvalidaException;
import com.example.pasa_la_pagina.exceptions.PublicacionNoEncontradaException;
import com.example.pasa_la_pagina.exceptions.UsuarioNoEncontradoException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

        // Handlers de Registro

        @ExceptionHandler(EmailEnUsoException.class)
        public ResponseEntity<Map<String, Object>> handleEmailEnUso(EmailEnUsoException ex) {
                Map<String, Object> body = new HashMap<>();
                body.put("timestamp", Instant.now().toString());
                body.put("errors", Map.of(
                                "email", ex.getMessage()));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }

        @ExceptionHandler(PasswordInvalidaException.class)
        public ResponseEntity<Map<String, Object>> handlePasswordInvalida(PasswordInvalidaException ex) {
                Map<String, Object> body = new HashMap<>();
                body.put("timestamp", Instant.now().toString());
                body.put("errors", Map.of(
                                "password", ex.getMessage()));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }

        // Handlers de Login

        @ExceptionHandler(CredencialesInvalidasException.class)
        public ResponseEntity<Map<String, Object>> handleCredencialesInvalidas(CredencialesInvalidasException ex) {
                Map<String, Object> body = new HashMap<>();
                body.put("timestamp", Instant.now().toString());
                body.put("error", ex.getMessage());

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }

        @ExceptionHandler(CodigoInvalidoException.class)
        public ResponseEntity<Map<String, Object>> handleCodigoInvalido(CodigoInvalidoException ex) {
                Map<String, Object> body = new HashMap<>();
                body.put("timestamp", Instant.now().toString());
                body.put("errors", Map.of(
                                "codigo", ex.getMessage()));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }

        @ExceptionHandler(CuentaVerificadaException.class)
        public ResponseEntity<Map<String, Object>> handleCuentaVerificada(CuentaVerificadaException ex) {
                Map<String, Object> body = new HashMap<>();
                body.put("timestamp", Instant.now().toString());
                body.put("errors", Map.of(
                                "cuenta_verificada", ex.getMessage()));
                return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
        }

        // Handlers de Registro/Login
        @ExceptionHandler(EmailNoVerificadoException.class)
        public ResponseEntity<Map<String, Object>> handleEmailNoVerificado(
                        EmailNoVerificadoException ex,
                        HttpServletRequest request) {
                Map<String, Object> body = new HashMap<>();
                body.put("timestamp", Instant.now().toString());

                if (request.getRequestURI().contains("/auth/register")) {
                        body.put("errors", Map.of(
                                        "email", ex.getMessage()));
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
                }

                body.put("errors", Map.of(
                                "email_verificado", ex.getMessage()));
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
        }

        // Handlers de Busqueda

        @ExceptionHandler(UsuarioNoEncontradoException.class)
        public ResponseEntity<Map<String, String>> handleUsuarioNoEncontrado(UsuarioNoEncontradoException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of(
                                                "timestamp", Instant.now().toString(),
                                                "status", String.valueOf(HttpStatus.NOT_FOUND.value()),
                                                "error", ex.getMessage()));
        }

        @ExceptionHandler(IntercambioNoEncontradoException.class)
        public ResponseEntity<Map<String, String>> handleIntercambioNoEncontrado(IntercambioNoEncontradoException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of(
                                                "timestamp", Instant.now().toString(),
                                                "status", String.valueOf(HttpStatus.NOT_FOUND.value()),
                                                "error", ex.getMessage()));
        }

        @ExceptionHandler(PublicacionNoEncontradaException.class)
        public ResponseEntity<Map<String, String>> handlePublicacionNoEncontrada(PublicacionNoEncontradaException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of(
                                                "timestamp", Instant.now().toString(),
                                                "status", String.valueOf(HttpStatus.NOT_FOUND.value()),
                                                "error", ex.getMessage()));
        }

        // Handlers de Intercambios

        @ExceptionHandler(IntercambioNoAutorizadoException.class)
        public ResponseEntity<Map<String, String>> handleIntercambioNoAutorizado(IntercambioNoAutorizadoException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of(
                                                "timestamp", Instant.now().toString(),
                                                "status", String.valueOf(HttpStatus.FORBIDDEN.value()),
                                                "error", ex.getMessage()));
        }

        @ExceptionHandler(IntercambioNoCancelableException.class)
        public ResponseEntity<Map<String, String>> handleIntercambioNoCancelable(IntercambioNoCancelableException ex) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(Map.of(
                                                "timestamp", Instant.now().toString(),
                                                "status", String.valueOf(HttpStatus.CONFLICT.value()),
                                                "error", ex.getMessage()));
        }

        @ExceptionHandler(IntercambioNoAceptableException.class)
        public ResponseEntity<Map<String, String>> handleIntercambioNoAceptable(IntercambioNoAceptableException ex) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(Map.of(
                                                "timestamp", Instant.now().toString(),
                                                "status", String.valueOf(HttpStatus.CONFLICT.value()),
                                                "error", ex.getMessage()));
        }

        @ExceptionHandler(IntercambioInvalidoException.class)
        public ResponseEntity<Map<String, String>> handleIntercambioInvalido(IntercambioInvalidoException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of(
                                                "timestamp", Instant.now().toString(),
                                                "status", String.valueOf(HttpStatus.BAD_REQUEST.value()),
                                                "error", ex.getMessage()));
        }

        @ExceptionHandler(ChatDeshabilitadoException.class)
        public ResponseEntity<Map<String, String>> handleChatDeshabilitado(ChatDeshabilitadoException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of(
                                                "timestamp", Instant.now().toString(),
                                                "status", String.valueOf(HttpStatus.FORBIDDEN.value()),
                                                "error", ex.getMessage()));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<Map<String, String>> handleOtrasExceptions(Exception ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", "Ocurrio un error inesperado"));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
                Map<String, Object> body = new HashMap<>();
                body.put("status", 400);
                body.put("errors", errors);
                return ResponseEntity.badRequest().body(body);
        }
}
