package com.example.pasa_la_pagina.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.pasa_la_pagina.entities.Enum.EstadoIntercambio;

@Entity
@Table(name = "Intercambio", uniqueConstraints = {
        @UniqueConstraint(name = "uk_intercambio_chat", columnNames = "chat_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    @JoinColumn(name = "usuario_solicitante_id", nullable = false, foreignKey = @ForeignKey(name = "fk_intercambio_usuario_solicitante"))
    private Usuario solicitante;

    @Column(name = "solicitante_concreto", nullable = false)
    @Builder.Default
    private Boolean solicitanteConcreto = false;

    // FK: usuario_propietario_id -> Usuarios.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_propietario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_intercambio_usuario_propietario"))
    private Usuario propietario;

    @Column(name = "propietario_concreto", nullable = false)
    @Builder.Default
    private Boolean propietarioConcreto = false;

    // FK: publicacion_id -> Publicacion.id (N:1)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "publicacion_id", nullable = false, foreignKey = @ForeignKey(name = "fk_intercambio_publicacion"))
    private Publicacion publicacion;

    // FK: chat_id -> Chat.id (1-1)
    @OneToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "chat_id", nullable = true, foreignKey = @ForeignKey(name = "fk_intercambio_chat"))
    private Chat chat;

    // 1-(0-2) inversa: Resenia tiene la FK intercambio_id
    @Builder.Default
    @OneToMany(mappedBy = "intercambio", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Resenia> resenias = new ArrayList<>();

    public Boolean isConcretado() {
        return solicitanteConcreto && propietarioConcreto;
    }

    @AssertTrue(message = "Un intercambio puede tener como máximo 2 reseñas.")
    public boolean isReseniasValidas() {
        return resenias.size() <= 2;
    }

    @AssertTrue(message = "La fecha_fin debe ser posterior a fecha_inicio.")
    public boolean isRangoFechasValido() {
        if (fechaInicio == null || fechaFin == null)
            return true; // fecha_fin opcional
        return fechaFin.isAfter(fechaInicio);
    }
}