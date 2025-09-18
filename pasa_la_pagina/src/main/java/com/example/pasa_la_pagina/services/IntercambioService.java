package com.example.pasa_la_pagina.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pasa_la_pagina.DTOs.requests.SolicitarIntercambioRequest;
import com.example.pasa_la_pagina.DTOs.response.IntercambioResponse;
import com.example.pasa_la_pagina.entities.Chat;
import com.example.pasa_la_pagina.entities.Intercambio;
import com.example.pasa_la_pagina.entities.Publicacion;
import com.example.pasa_la_pagina.entities.Usuario;
import com.example.pasa_la_pagina.entities.Enum.EstadoIntercambio;
import com.example.pasa_la_pagina.exceptions.IntercambioInvalidoException;
import com.example.pasa_la_pagina.exceptions.IntercambioNoAceptableException;
import com.example.pasa_la_pagina.exceptions.IntercambioNoAutorizadoException;
import com.example.pasa_la_pagina.exceptions.IntercambioNoCancelableException;
import com.example.pasa_la_pagina.exceptions.IntercambioNoEncontradoException;
import com.example.pasa_la_pagina.exceptions.PublicacionNoEncontradaException;
import com.example.pasa_la_pagina.exceptions.UsuarioNoEncontradoException;
import com.example.pasa_la_pagina.repositories.ChatRepository;
import com.example.pasa_la_pagina.repositories.IntercambioRepository;
import com.example.pasa_la_pagina.repositories.PublicacionRepository;
import com.example.pasa_la_pagina.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IntercambioService {

    private final IntercambioRepository intercambioRepository;
    private final UsuarioRepository usuarioRepository;
    private final PublicacionRepository publicacionRepository;
    private final ChatRepository chatRepository;

    @Transactional
    public IntercambioResponse solicitarIntercambio(SolicitarIntercambioRequest request, String solicitanteEmail) {
        Usuario solicitante = usuarioRepository.findByEmail(solicitanteEmail)
            .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + solicitanteEmail));

        Publicacion publicacion = publicacionRepository.findById(request.getPublicacionId())
            .orElseThrow(() -> new PublicacionNoEncontradaException(request.getPublicacionId()));

        Usuario propietario = publicacion.getUsuario();
        if (propietario == null) {
            throw new IntercambioInvalidoException("La publicacion no tiene un propietario asociado");
        }

        if (propietario.getId().equals(solicitante.getId())) {
            throw new IntercambioInvalidoException("No podes solicitar intercambio sobre tu propio material");
        }

        Chat chat = Chat.builder()
            .titulo("Intercambio publicacion " + publicacion.getId())
            .build();
        chat.setActivo(false);
        chat = chatRepository.save(chat);

        Intercambio intercambio = Intercambio.builder()
            .solicitante(solicitante)
            .propietario(propietario)
            .chat(chat)
            .estado(EstadoIntercambio.PENDIENTE)
            .build();

        Intercambio guardado = intercambioRepository.save(intercambio);
        return mapToResponse(guardado);
    }

    @Transactional
    public IntercambioResponse aceptarIntercambio(Long intercambioId, String propietarioEmail) {
        Intercambio intercambio = intercambioRepository.findById(intercambioId)
            .orElseThrow(() -> new IntercambioNoEncontradoException(intercambioId));

        Usuario propietario = usuarioRepository.findByEmail(propietarioEmail)
            .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + propietarioEmail));

        if (!intercambio.getPropietario().getId().equals(propietario.getId())) {
            throw new IntercambioNoAutorizadoException();
        }

        if (intercambio.getEstado() != EstadoIntercambio.PENDIENTE) {
            throw new IntercambioNoAceptableException(intercambio.getEstado());
        }

        Chat chat = intercambio.getChat();
        if (chat == null) {
            throw new IntercambioInvalidoException("El intercambio no posee un chat asociado");
        }

        if (chat.isActivo()) {
            throw new IntercambioNoAceptableException("El intercambio ya se encuentra habilitado");
        }

        chat.setActivo(true);
        intercambio.setFechaFin(null);

        Intercambio actualizado = intercambioRepository.save(intercambio);
        return mapToResponse(actualizado);
    }

    @Transactional
    public IntercambioResponse cancelarIntercambio(Long intercambioId, String usuarioEmail) {
        Intercambio intercambio = intercambioRepository.findById(intercambioId)
            .orElseThrow(() -> new IntercambioNoEncontradoException(intercambioId));

        Usuario actor = usuarioRepository.findByEmail(usuarioEmail)
            .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + usuarioEmail));

        boolean esSolicitante = intercambio.getSolicitante().getId().equals(actor.getId());
        boolean esPropietario = intercambio.getPropietario().getId().equals(actor.getId());

        if (!esSolicitante && !esPropietario) {
            throw new IntercambioNoAutorizadoException();
        }

        if (intercambio.getEstado() != EstadoIntercambio.PENDIENTE) {
            throw new IntercambioNoCancelableException(intercambio.getEstado());
        }

        intercambio.setEstado(EstadoIntercambio.CANCELADO);
        intercambio.setFechaFin(LocalDateTime.now());

        Chat chat = intercambio.getChat();
        if (chat != null && chat.isActivo()) {
            chat.setActivo(false);
        }

        Intercambio actualizado = intercambioRepository.save(intercambio);
        return mapToResponse(actualizado);
    }

    private IntercambioResponse mapToResponse(Intercambio intercambio) {
        Chat chat = intercambio.getChat();
        return IntercambioResponse.builder()
            .id(intercambio.getId())
            .solicitanteId(intercambio.getSolicitante() != null ? intercambio.getSolicitante().getId() : null)
            .propietarioId(intercambio.getPropietario() != null ? intercambio.getPropietario().getId() : null)
            .chatId(chat != null ? chat.getId() : null)
            .fechaInicio(intercambio.getFechaInicio())
            .fechaFin(intercambio.getFechaFin())
            .estado(intercambio.getEstado())
            .chatActivo(chat != null && chat.isActivo())
            .build();
    }
}
