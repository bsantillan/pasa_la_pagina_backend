package com.example.pasa_la_pagina.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;

@Entity
@Table(name = "Mensaje")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Mensaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_inicio", nullable = false)
    @Builder.Default
    private LocalDateTime fechaInicio = LocalDateTime.now();

    @Column(name = "contenido", nullable = false, length = 1000)
    private String contenido;

    // FK: mensaje.id -> Chat.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_id", nullable = false, foreignKey = @ForeignKey(name = "fk_mensaje_chat"))
    private Chat chat;

    // Relación muchos a uno (opcional) con Usuario
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "usuario_id", nullable = true, foreignKey = @ForeignKey(name = "fk_mensaje_usuario"))
    private Usuario usuario;

    // Validación: el usuario debe ser solicitante o propietario del intercambio
    @AssertTrue(message = "El usuario del mensaje debe ser solicitante o propietario del intercambio.")
    private boolean isUsuarioValido() {
        if (usuario == null || chat == null || chat.getIntercambio() == null) return true;
        Long userId = usuario.getId();
        Intercambio intercambio = chat.getIntercambio();
        return userId.equals(intercambio.getSolicitante().getId()) 
            || userId.equals(intercambio.getPropietario().getId());
    }
}
