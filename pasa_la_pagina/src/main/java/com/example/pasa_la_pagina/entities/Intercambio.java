package com.example.pasa_la_pagina.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;

import java.time.LocalDateTime;

import com.example.pasa_la_pagina.entities.Enum.EstadoIntercambio;

@Entity
@Table(
    name = "Intercambio",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_intercambio_chat", columnNames = "chat_id")
    }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Intercambio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "fecha_inicio", nullable = false)
    @Builder.Default
    private LocalDateTime fechaInicio = LocalDateTime.now();

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoIntercambio estado = EstadoIntercambio.PENDIENTE;

    // FK: usuario_solicitante_id -> Usuarios.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "usuario_solicitante_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_intercambio_usuario_solicitante")
    )
    private Usuario solicitante;

    // FK: usuario_propietario_id -> Usuarios.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "usuario_propietario_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_intercambio_usuario_propietario")
    )
    private Usuario propietario;

    // FK: chat_id -> Chat.id (1-1)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "chat_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_intercambio_chat")
    )
    private Chat chat;

    // 1-1 inversa: Resenia tiene la FK intercambio_id
    @OneToOne(mappedBy = "intercambio", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Resenia resenia;


    @AssertTrue(message = "El solicitante y el propietario deben ser usuarios distintos.")
    private boolean isSolicitanteDistintoDePropietario() {
        if (solicitante == null || propietario == null) return true;
        return !solicitante.getId().equals(propietario.getId());
    }

    @AssertTrue(message = "La fecha_fin debe ser posterior a fecha_inicio.")
    private boolean isRangoFechasValido() {
        if (fechaInicio == null || fechaFin == null) return true; // fecha_fin opcional
        return fechaFin.isAfter(fechaInicio);
    }
}