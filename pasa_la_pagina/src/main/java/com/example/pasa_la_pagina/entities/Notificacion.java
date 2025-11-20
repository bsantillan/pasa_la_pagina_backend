package com.example.pasa_la_pagina.entities;

import java.time.LocalDateTime;

import com.example.pasa_la_pagina.entities.Enum.TipoNotificacion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Notificacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_notificacion", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoNotificacion tipoNotificacion;

    @Column(name = "titulo", nullable = false, length = 100)
    private String titulo;

    @Column(name = "contenido", nullable = false, length = 1000)
    private String contenido;

    @Column(name = "fecha_notificacion", nullable = false)
    private LocalDateTime fecha_notificacion;

    @OneToOne
    @JoinColumn(name = "receptor_id", nullable = false)
    private Usuario receptor;

    @OneToOne
    @JoinColumn(name = "emisor_id", nullable = false)
    private Usuario emisor;

    @OneToOne
    @JoinColumn(name = "intercambio_id", nullable = true)
    private Intercambio intercambio;

    @OneToOne
    @JoinColumn(name = "mensaje_id", nullable = true)
    private Mensaje Mensaje;

    @OneToOne
    @JoinColumn(name = "chat_id", nullable = true)
    private Chat chat;
}
