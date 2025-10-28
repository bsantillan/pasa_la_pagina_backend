package com.example.pasa_la_pagina.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.pasa_la_pagina.DTOs.response.MensajeResponse;
import com.example.pasa_la_pagina.DTOs.response.PageRecuperarResponse;
import com.example.pasa_la_pagina.entities.Chat;
import com.example.pasa_la_pagina.entities.Mensaje;
import com.example.pasa_la_pagina.entities.Usuario;
import com.example.pasa_la_pagina.exceptions.IntercambioNoAutorizadoException;
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
        // { changed code } - inyectamos SimpMessagingTemplate en el servicio para
        // centralizar la difusión
        private final SimpMessagingTemplate messagingTemplate;

        private PageRecuperarResponse mapToPageResponse(Page<?> pages) {
                PageRecuperarResponse response = new PageRecuperarResponse();
                response.setContent(pages.getContent());
                response.setSize(pages.getSize());
                response.setTotalElements(pages.getTotalElements());
                response.setTotalPages(pages.getTotalPages());
                return response;
        }

        private MensajeResponse mapToResponseMensaje(Mensaje mensaje) {
                MensajeResponse response = new MensajeResponse();

                response.setContenido(mensaje.getContenido());
                response.setUsuarioId(mensaje.getUsuario().getId());
                response.setUsuario(mensaje.getUsuario().getApellido() + " " + mensaje.getUsuario().getNombre());
                response.setFechaInicio(mensaje.getFechaInicio());

                return response;
        }

        /**
         * Guarda el mensaje y notifica en tiempo real solo a los dos participantes del
         * chat.
         *
         * Flujo:
         * 1) Persistir Mensaje.
         * 2) Construir MensajeResponse.
         * 3) Registrar una callback afterCommit para enviar el payload vía
         * convertAndSendToUser
         * (asegura que no se difunda antes de confirmar la transacción).
         *
         * Destino para el cliente (suscripción):
         * - /user/queue/chats/{chatId}
         *
         * En el cliente STOMP: stompClient.subscribe('/user/queue/chats/{chatId}', ...)
         */
        public MensajeResponse saveMessage(Long chatId, String senderEmail, String content) {
                Chat chat = chatRepository.findById(chatId)
                                .orElseThrow(() -> new IllegalArgumentException("Chat no existe: " + chatId));

                Usuario usuario = usuarioRepository.findByEmail(senderEmail)
                                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe: " + senderEmail));

                if (!chat.getIntercambio().getPropietario().getEmail().equals(usuario.getEmail())
                                || !chat.getIntercambio().getSolicitante().getEmail().equals(usuario.getEmail())) {
                        throw new IntercambioNoAutorizadoException();
                }

                Mensaje mensaje = Mensaje.builder()
                                .chat(chat)
                                .usuario(usuario)
                                .contenido(content)
                                .fechaInicio(LocalDateTime.now())
                                .build();

                Mensaje saved = mensajeRepository.save(mensaje);
                MensajeResponse resp = mapToResponseMensaje(saved);

                // Determinar emails (o nombres de usuario) de los dos participantes del chat
                String sender = usuario.getEmail(); // asumimos que Principal.getName() = email

                messagingTemplate.convertAndSendToUser(sender, "/queue/chats/" + chatId, resp);

                return resp;
        }

        public PageRecuperarResponse getMessages(Long chatId, int page, int size) {
                chatRepository.findById(chatId)
                                .orElseThrow(() -> new IllegalArgumentException("Chat no existe: " + chatId));

                Pageable pegeable = PageRequest.of(page, size);
                Page<Mensaje> mensajes = mensajeRepository.findByChatIdOrderByFechaInicioDesc(chatId, pegeable);
                return mapToPageResponse(mensajes);
        }
}