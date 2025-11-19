package com.example.pasa_la_pagina.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pasa_la_pagina.DTOs.requests.BuscarIntercambioRequest;
import com.example.pasa_la_pagina.DTOs.response.PageRecuperarResponse;
import com.example.pasa_la_pagina.DTOs.response.RecuperarIntercambioResponse;
import com.example.pasa_la_pagina.entities.Chat;
import com.example.pasa_la_pagina.entities.Intercambio;
import com.example.pasa_la_pagina.entities.Publicacion;
import com.example.pasa_la_pagina.entities.Usuario;
import com.example.pasa_la_pagina.entities.Enum.EstadoIntercambio;
import com.example.pasa_la_pagina.entities.Enum.TitulosNotificaciones;
import com.example.pasa_la_pagina.exceptions.IntercambioInvalidoException;
import com.example.pasa_la_pagina.exceptions.IntercambioNoAceptableException;
import com.example.pasa_la_pagina.exceptions.IntercambioNoAutorizadoException;
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
    private final NotificacionService notificacionService;

    private PageRecuperarResponse mapToPageResponse(Page<?> pages) {
        PageRecuperarResponse response = new PageRecuperarResponse();
        response.setContent(pages.getContent());
        response.setSize(pages.getSize());
        response.setTotalElements(pages.getTotalElements());
        response.setTotalPages(pages.getTotalPages());
        return response;
    }

    private RecuperarIntercambioResponse mapToResponseRecuperarIntercambio(Intercambio intercambio, Long usuario_id) {
        RecuperarIntercambioResponse response = new RecuperarIntercambioResponse();
        if (intercambio.getSolicitante().getId().equals(usuario_id)) {
            response.setRolUsuario("Solicitante");
            response.setUsuario(
                    intercambio.getPropietario().getApellido() + " " + intercambio.getPropietario().getNombre());
            response.setUsuarioEmail(intercambio.getPropietario().getEmail());

        } else {
            response.setRolUsuario("Propietario");
            response.setUsuario(
                    intercambio.getSolicitante().getApellido() + " " + intercambio.getSolicitante().getNombre());
            response.setUsuarioEmail(intercambio.getSolicitante().getEmail());
        }
        response.setId(intercambio.getId());

        if (intercambio.getChat() != null) {
            response.setChatId(intercambio.getChat().getId());
        } else {
            response.setChatId(null); // o podés omitirlo si el DTO permite null
        }

        response.setEstadoIntercambio(intercambio.getEstado());
        response.setFechaInicio(intercambio.getFechaInicio());
        response.setTituloPublicaicon(intercambio.getPublicacion().getMaterial().getTitulo());
        response.setPropietarioConcreto(intercambio.getPropietarioConcreto());
        response.setSolicitanteConcreto(intercambio.getSolicitanteConcreto());
        return response;
    }

    @Transactional
    public void solicitarIntercambio(Long publicacionId, String solicitanteEmail) {
        Usuario solicitante = usuarioRepository.findByEmail(solicitanteEmail)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + solicitanteEmail));

        Publicacion publicacion = publicacionRepository.findById(publicacionId)
                .orElseThrow(() -> new PublicacionNoEncontradaException(publicacionId));

        Usuario propietario = publicacion.getUsuario();
        if (propietario == null) {
            throw new IntercambioInvalidoException("La publicacion no tiene un propietario asociado");
        }

        if (propietario.getId().equals(solicitante.getId())) {
            throw new IntercambioInvalidoException("No podes solicitar intercambio sobre tu propio material");
        }

        intercambioRepository.findByPublicacion_IdAndSolicitante_Id(publicacionId, solicitante.getId())
                .ifPresent(i -> {
                    throw new IntercambioInvalidoException(
                            "Ya existe un intercambio solicitado por este usuario para esta publicación");
                });

        Intercambio intercambio = Intercambio.builder()
                .publicacion(publicacion)
                .solicitante(solicitante)
                .propietario(propietario)
                .build();
        intercambioRepository.save(intercambio);

        String mensajeNotificacion = "¡Hola " + propietario.getNombre() + "!\n"
                + solicitante.getNombre() + " quiere intercambiar contigo";

        notificacionService.enviarNotificacionAUsuario(
                TitulosNotificaciones.SOLICITUD_INTERCAMBIO,
                mensajeNotificacion,
                propietario.getId());
    }

    @Transactional
    public void aceptarIntercambio(Long intercambioId, String propietarioEmail) {
        Intercambio intercambio = intercambioRepository.findById(intercambioId)
                .orElseThrow(() -> new IntercambioNoEncontradoException(intercambioId));

        if (intercambio.getEstado() != EstadoIntercambio.PENDIENTE) {
            throw new IntercambioNoAceptableException(intercambio.getEstado());
        }

        if (!intercambio.getPropietario().getEmail().equals(propietarioEmail)) {
            throw new IntercambioNoAutorizadoException();
        }

        intercambio.setEstado(EstadoIntercambio.ACEPTADO);
        intercambio.setChat(Chat.builder()
                .titulo("Chat")
                .build());
        intercambioRepository.save(intercambio);

        String mensajeNotificacion = "¡Genial, " + intercambio.getSolicitante().getNombre() + "!\n"
                + intercambio.getPropietario().getNombre() + " ha aceptado tu solicitud de intercambio.\\n" + //
                "Prepárate para realizar el intercambio.";

        notificacionService.enviarNotificacionAUsuario(
                TitulosNotificaciones.INTERCAMBIO_ACEPTADO,
                mensajeNotificacion,
                intercambio.getSolicitante().getId());
    }

    @Transactional
    public void cancelarIntercambio(Long intercambioId, String usuarioEmail) {
        Usuario usuario = usuarioRepository.findByEmail(usuarioEmail)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + usuarioEmail));

        Intercambio intercambio = intercambioRepository.findById(intercambioId)
                .orElseThrow(() -> new IntercambioNoEncontradoException(intercambioId));

        if (intercambio.getEstado() != EstadoIntercambio.ACEPTADO) {
            throw new IntercambioNoAceptableException(intercambio.getEstado());
        }

        if (!intercambio.getPropietario().getEmail().equals(usuarioEmail)
                && !intercambio.getSolicitante().getEmail().equals(usuarioEmail)) {
            throw new IntercambioNoAutorizadoException();
        }

        Chat chat = intercambio.getChat();
        intercambio.setChat(null); 
        chatRepository.delete(chat);

        intercambio.setEstado(EstadoIntercambio.CANCELADO);
        intercambio.setFechaFin(LocalDateTime.now());

        intercambioRepository.save(intercambio);

        Usuario receptor;
        if (intercambio.getPropietario().getId().equals(usuario.getId())) {
            receptor = intercambio.getSolicitante();
        } else {
            receptor = intercambio.getPropietario();
        }

        String mensajeNotificacion = "Hola " + receptor.getNombre() + ", " + usuario.getNombre()
                + " ha decidido cancelar el intercambio.\\n"
                + "Puedes seguir explorando otras opciones disponibles.";

        notificacionService.enviarNotificacionAUsuario(
                TitulosNotificaciones.INTERCAMBIO_CANCELADO,
                mensajeNotificacion,
                receptor.getId());
    }

    @Transactional
    public void rechazarIntercambio(Long intercambioId, String usuarioEmail) {
        Usuario usuario = usuarioRepository.findByEmail(usuarioEmail)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + usuarioEmail));

        Intercambio intercambio = intercambioRepository.findById(intercambioId)
                .orElseThrow(() -> new IntercambioNoEncontradoException(intercambioId));

        if (intercambio.getEstado() != EstadoIntercambio.PENDIENTE) {
            throw new IntercambioNoAceptableException(intercambio.getEstado());
        }

        if (!intercambio.getPropietario().getEmail().equals(usuarioEmail)
                && !intercambio.getSolicitante().getEmail().equals(usuarioEmail)) {
            throw new IntercambioNoAutorizadoException();
        }

        intercambio.setEstado(EstadoIntercambio.RECHAZADO);
        intercambio.setFechaFin(LocalDateTime.now());

        intercambioRepository.save(intercambio);

        Usuario receptor;
        if (intercambio.getPropietario().getId().equals(usuario.getId())) {
            receptor = intercambio.getSolicitante();
        } else {
            receptor = intercambio.getPropietario();
        }

        String mensajeNotificacion = "Hola " + receptor.getNombre() + ", lamentablemente " + usuario.getNombre()
                + " ha rechazado tu solicitud de intercambio.\\n" + //
                "¡Sigue intentándolo, seguro encontrarás otro intercambio pronto!";

        notificacionService.enviarNotificacionAUsuario(
                TitulosNotificaciones.INTERCAMBIO_RECHAZADO,
                mensajeNotificacion,
                receptor.getId());
    }

    @Transactional
    public void concretarIntercambio(Long intercambioId, String usuarioEmail) {
        Usuario usuario = usuarioRepository.findByEmail(usuarioEmail)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + usuarioEmail));

        Intercambio intercambio = intercambioRepository.findById(intercambioId)
                .orElseThrow(() -> new IntercambioNoEncontradoException(intercambioId));

        if (!intercambio.getSolicitante().getEmail().equals(usuarioEmail)
                && !intercambio.getPropietario().getEmail().equals(usuarioEmail)) {
            throw new IntercambioNoAutorizadoException();
        }

        Usuario receptor;
        if (intercambio.getSolicitante().getEmail().equals(usuarioEmail)) {
            intercambio.setSolicitanteConcreto(true);
            receptor = intercambio.getPropietario();
        } else {
            intercambio.setPropietarioConcreto(true);
            receptor = intercambio.getSolicitante();
        }

        if (intercambio.isConcretado()) {
            intercambio.setEstado(EstadoIntercambio.CONCRETADO);
            chatRepository.delete(intercambio.getChat());
        }

        String mensajeNotificacion = "¡Hola " + receptor.getNombre() + "! " + usuario.getNombre()
                + " confirmó que el intercambio se concretó.\\n"
                + "¡Gracias por ser parte de la comunidad!";

        notificacionService.enviarNotificacionAUsuario(
                TitulosNotificaciones.INTERCAMBIO_CONCRETADO,
                mensajeNotificacion,
                receptor.getId());

        intercambioRepository.save(intercambio);
    }

    @Transactional
    public PageRecuperarResponse buscarIntercambios(int page, int size, String email,
            BuscarIntercambioRequest filtros) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + email));

        Sort sort = Sort.by(Sort.Direction.DESC, "fechaInicio");

        Pageable pageable = PageRequest.of(page, size, sort);

        if (filtros == null) {
            return mapToPageResponse(intercambioRepository.buscarTodosIntercambios(pageable, usuario.getId())
                    .map(i -> mapToResponseRecuperarIntercambio(i, usuario.getId())));
        }

        if (filtros.getOrdenDescendente() != null && filtros.getOrdenarPor() != null) {
            Sort.Direction dir = filtros.getOrdenDescendente() ? Sort.Direction.DESC : Sort.Direction.ASC;
            switch (filtros.getOrdenarPor()) {
                case FECHA_INICIO:
                    sort = Sort.by(dir, "fechaInicio");
                    break;
                case TITULO_PUBLICACION:
                    sort = Sort.by(dir, "publicacion.material.titulo");
                    break;
                case ESTADO:
                    sort = Sort.by(dir, "estado");
                    break;
            }
        }

        List<EstadoIntercambio> estados = (filtros.getEstadosIntercambio() == null
                || filtros.getEstadosIntercambio().isEmpty())
                        ? null
                        : filtros.getEstadosIntercambio();

        Page<Intercambio> intercambios = intercambioRepository.buscarIntercambiosConFiltros(
                usuario.getId(),
                estados,
                filtros.getFechaInicio(),
                filtros.getTituloPublicacion(),
                filtros.getUsuario(),
                pageable);

        return mapToPageResponse(intercambios.map(i -> mapToResponseRecuperarIntercambio(i, usuario.getId())));
    }
}
