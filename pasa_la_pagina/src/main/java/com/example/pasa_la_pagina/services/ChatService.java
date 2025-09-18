package com.example.pasa_la_pagina.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.pasa_la_pagina.DTOs.chat.ChatMessageResponse;
import com.example.pasa_la_pagina.entities.Chat;
import com.example.pasa_la_pagina.exceptions.ChatDeshabilitadoException;
import com.example.pasa_la_pagina.entities.Mensaje;
import com.example.pasa_la_pagina.entities.Usuario;
import com.example.pasa_la_pagina.repositories.ChatRepository;
import com.example.pasa_la_pagina.repositories.MensajeRepository;
import com.example.pasa_la_pagina.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final MensajeRepository mensajeRepository;
    private final UsuarioRepository usuarioRepository;

    public ChatMessageResponse saveMessage(Long chatId, String senderEmail, String content) {
        Chat chat = chatRepository.findById(chatId)
            .orElseThrow(() -> new IllegalArgumentException("Chat no existe: " + chatId));

        if (!chat.isActivo()) {
            throw new ChatDeshabilitadoException(chatId);
        }

        Usuario sender = usuarioRepository.findByEmail(senderEmail)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no existe: " + senderEmail));

        Mensaje mensaje = Mensaje.builder()
            .chat(chat)
            .usuario(sender)
            .contenido(content)
            .fechaInicio(LocalDateTime.now())
            .build();

        Mensaje saved = mensajeRepository.save(mensaje);

        return ChatMessageResponse.builder()
            .id(saved.getId())
            .chatId(chatId)
            .content(saved.getContenido())
            .senderEmail(senderEmail)
            .createdAt(saved.getFechaInicio())
            .build();
    }

    public Page<ChatMessageResponse> getMessages(Long chatId, int page, int size) {
        Chat chat = chatRepository.findById(chatId)
            .orElseThrow(() -> new IllegalArgumentException("Chat no existe: " + chatId));

        if (!chat.isActivo()) {
            throw new ChatDeshabilitadoException(chatId);
        }

        Page<Mensaje> mensajes = mensajeRepository.findByChatIdOrderByFechaInicioDesc(chatId, PageRequest.of(page, size));
        return new PageImpl<>(
            mensajes.getContent().stream().map(m -> ChatMessageResponse.builder()
                .id(m.getId())
                .chatId(chatId)
                .content(m.getContenido())
                .senderEmail(m.getUsuario() != null ? m.getUsuario().getEmail() : null)
                .createdAt(m.getFechaInicio())
                .build()
            ).toList(),
            mensajes.getPageable(),
            mensajes.getTotalElements()
        );
    }

    public Chat createChat(String title) {
        Chat chat = Chat.builder().titulo(title).build();
        return chatRepository.save(chat);
    }
}

