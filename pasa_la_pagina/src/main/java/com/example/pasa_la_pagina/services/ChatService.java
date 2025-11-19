package com.example.pasa_la_pagina.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pasa_la_pagina.DTOs.requests.ChatMessage;
import com.example.pasa_la_pagina.DTOs.response.MensajeResponse;
import com.example.pasa_la_pagina.entities.Chat;
import com.example.pasa_la_pagina.entities.Mensaje;
import com.example.pasa_la_pagina.entities.Usuario;
import com.example.pasa_la_pagina.entities.Enum.TitulosNotificaciones;
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
        private final SimpMessagingTemplate messagingTemplate;
        private final NotificacionService notificacionService;

        @Transactional
        public MensajeResponse guardarMensaje(ChatMessage chatMessage, String usuarioEmail) {
                Chat chat = chatRepository.findById(chatMessage.getChatId())
                                .orElseThrow(() -> new RuntimeException("Chat no encontrado"));

                Usuario usuario = usuarioRepository.findByEmail(usuarioEmail)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                Mensaje mensaje = Mensaje.builder()
                                .contenido(chatMessage.getContent())
                                .chat(chat)
                                .usuario(usuario)
                                .fechaInicio(LocalDateTime.now())
                                .build();

                mensajeRepository.save(mensaje);

                Usuario receptor;
                if (chat.getIntercambio().getPropietario().getId().equals(usuario.getId())) {
                        receptor = chat.getIntercambio().getSolicitante();
                } else {
                        receptor = chat.getIntercambio().getPropietario();
                }

                String mensajeNotificacion = "¡Hola " + receptor.getNombre() + "!\n"
                                + usuario.getNombre() + " te ha enviado un nuevo mensaje:\n"
                                + chatMessage.getContent();

                notificacionService.enviarNotificacionAUsuario(
                                TitulosNotificaciones.NUEVO_MENSAJE,
                                mensajeNotificacion,
                                receptor.getId());

                return new MensajeResponse(
                                mensaje.getId(),
                                mensaje.getContenido(),
                                usuario.getEmail(),
                                mensaje.getFechaInicio());
        }

        public void sendToSubscribers(Long chatId, MensajeResponse mensaje) {
                messagingTemplate.convertAndSend("/topic/chat/" + chatId, mensaje);
        }

        @Transactional(readOnly = true)
        public List<MensajeResponse> obtenerMensajes(Long chatId) {
                Chat chat = chatRepository.findById(chatId)
                                .orElseThrow(() -> new RuntimeException("Chat no encontrado"));

                return chat.getMensajes().stream()
                                .map(m -> new MensajeResponse(
                                                m.getId(),
                                                m.getContenido(),
                                                m.getUsuario().getEmail(),
                                                m.getFechaInicio()))
                                .collect(Collectors.toList());
        }
}
