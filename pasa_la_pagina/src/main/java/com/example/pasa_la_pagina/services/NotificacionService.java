package com.example.pasa_la_pagina.services;

import java.time.LocalDateTime;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.pasa_la_pagina.DTOs.requests.DeleteNotificacionRequest;
import com.example.pasa_la_pagina.DTOs.response.RecuperarNotificacionResponse;
import com.example.pasa_la_pagina.entities.Chat;
import com.example.pasa_la_pagina.entities.Intercambio;
import com.example.pasa_la_pagina.entities.Mensaje;
import com.example.pasa_la_pagina.entities.Notificacion;
import com.example.pasa_la_pagina.entities.Usuario;
import com.example.pasa_la_pagina.entities.Enum.TipoNotificacion;
import com.example.pasa_la_pagina.exceptions.UsuarioNoEncontradoException;
import com.example.pasa_la_pagina.repositories.NotificacionRepository;
import com.example.pasa_la_pagina.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificacionService {

        private final SimpMessagingTemplate messagingTemplate;
        private final UsuarioRepository usuarioRepository;
        private final NotificacionRepository notificacionRepository;

        private RecuperarNotificacionResponse mapToResponseRecuperarNotificacion(Notificacion notificacion) {
                RecuperarNotificacionResponse response = new RecuperarNotificacionResponse();
                response.setId(notificacion.getId());
                response.setFecha(notificacion.getFecha_notificacion());
                response.setUsuario(
                                notificacion.getEmisor().getApellido() + " " + notificacion.getEmisor().getNombre());
                response.setMensaje(notificacion.getContenido());
                response.setIntercambio_id(notificacion.getIntercambio() != null
                                ? notificacion.getIntercambio().getId()
                                : null);
                response.setChat_id(notificacion.getChat() != null
                                ? notificacion.getChat().getId()
                                : null);
                response.setMensaje_id(notificacion.getMensaje() != null
                                ? notificacion.getMensaje().getId()
                                : null);
                response.setTitulo(notificacion.getTitulo());
                response.setTipo_notificacion(notificacion.getTipoNotificacion());

                return response;
        }

        private String generarMensajeNotificacion(TipoNotificacion tipo, String contenidoMensaje, String nombreReceptor,
                        String nombreEmisor) {

                switch (tipo) {

                        case INTERCAMBIO_CONCRETADO:
                                return "¡Hola " + nombreReceptor + "! \n"
                                                + nombreEmisor
                                                + " confirmó que el intercambio se concretó. \n"
                                                + "¡Gracias por ser parte de la comunidad!";

                        case INTERCAMBIO_RECHAZADO:
                                return "Hola " + nombreReceptor + ", lamentablemente " + nombreEmisor
                                                + " ha rechazado tu solicitud de intercambio. \n"
                                                + "¡Sigue intentándolo, seguro encontrarás otro intercambio pronto!";

                        case INTERCAMBIO_CANCELADO:
                                return "Hola " + nombreReceptor + ", "
                                                + nombreEmisor
                                                + " ha decidido cancelar el intercambio. \n"
                                                + "Puedes seguir explorando otras opciones disponibles.";

                        case INTERCAMBIO_ACEPTADO:
                                return "¡Genial, " + nombreReceptor + "! \n"
                                                + nombreEmisor
                                                + " ha aceptado tu solicitud de intercambio. \n"
                                                + "Prepárate para realizar el intercambio.";

                        case SOLICITUD_INTERCAMBIO:
                                return "¡Hola " + nombreReceptor + "! \n"
                                                + nombreEmisor + " quiere intercambiar contigo.";

                        case NUEVO_MENSAJE:
                                return nombreEmisor + " te ha enviado un nuevo mensaje: \n"
                                                + contenidoMensaje;
                        default:
                                return "Tienes una nueva notificación.";
                }
        }

        public void enviarNotificacionAUsuario(Notificacion notificacion) {

                RecuperarNotificacionResponse notificacionResponse = mapToResponseRecuperarNotificacion(notificacion);

                // canal único por usuario
                messagingTemplate.convertAndSend("/topic/notificaciones/" + notificacion.getReceptor().getId(),
                                notificacionResponse);
        }

        public Notificacion crearNotificacion(TipoNotificacion tipoNotificacion, Intercambio intercambio,
                        Mensaje mensaje, Chat chat, Usuario receptor, Usuario emisor) {

                String contenidoMensaje = null;

                if (mensaje != null) {
                        contenidoMensaje = mensaje.getContenido();
                }

                String contenidoNotificacion = generarMensajeNotificacion(
                                tipoNotificacion,
                                contenidoMensaje,
                                receptor.getApellido() + " " + receptor.getNombre(),
                                emisor.getApellido() + " " + emisor.getNombre());

                Notificacion notificacion = Notificacion.builder()
                                .tipoNotificacion(tipoNotificacion)
                                .contenido(contenidoNotificacion)
                                .titulo(tipoNotificacion.getTitulo())
                                .intercambio(intercambio)
                                .chat(chat != null
                                                ? chat
                                                : null)
                                .Mensaje(mensaje)
                                .receptor(receptor)
                                .emisor(emisor)
                                .fecha_notificacion(LocalDateTime.now())
                                .build();

                return notificacionRepository.save(notificacion);
        }

        public void eliminarNotificacionById(DeleteNotificacionRequest request, String userEmail) {
                Usuario usuario = usuarioRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new UsuarioNoEncontradoException(
                                                "Usuario no encontrado: " + userEmail));
                Notificacion notificacion = notificacionRepository.findById(request.getId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "No se encontro una notificacion con el id: " + request.getId()));
                if (notificacion.getReceptor().getId() != usuario.getId()) {
                        throw new IllegalArgumentException(
                                        "La notificacion no es del usuario con id: " + usuario.getId());
                }
                notificacionRepository.delete(notificacion);
        }
}
