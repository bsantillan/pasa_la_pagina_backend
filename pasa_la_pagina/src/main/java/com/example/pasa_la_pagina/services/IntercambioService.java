package com.example.pasa_la_pagina.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pasa_la_pagina.DTOs.requests.BuscarIntercambioRequest;
import com.example.pasa_la_pagina.DTOs.response.IntercambioEstadoEvento;
import com.example.pasa_la_pagina.DTOs.response.PageRecuperarResponse;
import com.example.pasa_la_pagina.DTOs.response.RecuperarIntercambioResponse;
import com.example.pasa_la_pagina.entities.Chat;
import com.example.pasa_la_pagina.entities.Intercambio;
import com.example.pasa_la_pagina.entities.Publicacion;
import com.example.pasa_la_pagina.entities.Usuario;
import com.example.pasa_la_pagina.entities.Enum.EstadoIntercambio;
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

    private final SimpMessagingTemplate webSocketTemplate;

    @Transactional
    public String generarCodigoValidacion(Long intercambioId, String emailUsuario) {
        Intercambio intercambio = intercambioRepository.findById(intercambioId)
                .orElseThrow(() -> new IntercambioNoEncontradoException(intercambioId));

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        if (!intercambio.getPropietario().getEmail().equals(emailUsuario)) {
            throw new IntercambioNoAutorizadoException();
        }

        if (intercambio.getEstado() != EstadoIntercambio.ACEPTADO) {
            throw new IntercambioNoAceptableException("Solo se puede generar código para intercambios aceptados");
        }

        intercambio.generarCodigoValidacion();
        intercambio.setPropietarioConcreto(true);
        intercambioRepository.save(intercambio);

        IntercambioEstadoEvento evento = IntercambioEstadoEvento.builder()
                .intercambioId(intercambioId)
                .estado(intercambio.getEstado())
                .puedeDejarResena(false)
                .mensaje("Se ha generado un código de validación")
                .build();

        webSocketTemplate.convertAndSend(
            "/topic/intercambio/" + intercambioId + "/estado", 
            evento
        );

        webSocketTemplate.convertAndSendToUser(
            intercambio.getSolicitante().getEmail(),
            "/user/queue/intercambio-estado",
            evento
        );

        webSocketTemplate.convertAndSendToUser(
            intercambio.getPropietario().getEmail(),
            "/user/queue/intercambio-estado",
            evento
        );

        return intercambio.getCodigoValidacion();
    }

    @Transactional
    public void validarIntercambioConCodigo(Long intercambioId, String codigo, String emailUsuario) {
        Intercambio intercambio = intercambioRepository.findById(intercambioId)
                .orElseThrow(() -> new IntercambioNoEncontradoException(intercambioId));

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        if (!intercambio.getSolicitante().getEmail().equals(emailUsuario)) {
            throw new IntercambioNoAutorizadoException();
        }

        if (intercambio.esCodigoExpirado()) {
            throw new IntercambioInvalidoException("El código de validación ha expirado");
        }

        if (!intercambio.esCodigoValido(codigo)) {
            throw new IntercambioInvalidoException("Código de validación inválido");
        }

        intercambio.setSolicitanteConcreto(true);

        if (intercambio.isConcretado()) {
            intercambio.setEstado(EstadoIntercambio.CONCRETADO);
            intercambio.setFechaFin(LocalDateTime.now());
            intercambio.setCodigoValidacion(null);
            intercambio.setCodigoExpiracion(null);
        }

        intercambioRepository.save(intercambio);

        IntercambioEstadoEvento evento = IntercambioEstadoEvento.builder()
                .intercambioId(intercambioId)
                .estado(intercambio.getEstado())
                .puedeDejarResena(intercambio.getEstado() == EstadoIntercambio.CONCRETADO)
                .mensaje("Intercambio concretado exitosamente")
                .build();

        webSocketTemplate.convertAndSendToUser(
        intercambio.getPropietario().getEmail(),
        "/queue/intercambio-estado",
        evento
        );

        webSocketTemplate.convertAndSendToUser(
            intercambio.getSolicitante().getEmail(),
            "/queue/intercambio-estado",
            evento
        );

    // 👇 Opcional: enviar también al topic general
        webSocketTemplate.convertAndSend(
            "/topic/intercambio/" + intercambioId + "/estado", 
            evento
        );
    }

    @Transactional
    public void verificarYActualizarExpirados() {
        List<Intercambio> intercambios = intercambioRepository.findByEstadoAndCodigoValidacionIsNotNull(EstadoIntercambio.ACEPTADO);
        
        for (Intercambio intercambio : intercambios) {
            if (intercambio.debeCambiarARechazado()) {
                intercambio.setEstado(EstadoIntercambio.RECHAZADO);
                intercambio.setFechaFin(LocalDateTime.now());
                intercambio.setCodigoValidacion(null);
                intercambio.setCodigoExpiracion(null);
                
                intercambioRepository.save(intercambio);
                
                try {
                IntercambioEstadoEvento evento = IntercambioEstadoEvento.builder()
                        .intercambioId(intercambio.getId())
                        .estado(EstadoIntercambio.RECHAZADO)
                        .puedeDejarResena(true)
                        .mensaje("Intercambio rechazado por expiración del código de validación")
                        .build();

                webSocketTemplate.convertAndSend(
                    "/topic/intercambio/" + intercambio.getId() + "/estado", 
                    evento
                );

                // Notificar a ambos participantes
                webSocketTemplate.convertAndSendToUser(
                    intercambio.getSolicitante().getEmail(),
                    "/queue/intercambio-estado",
                    evento
                );

                webSocketTemplate.convertAndSendToUser(
                    intercambio.getPropietario().getEmail(),
                    "/queue/intercambio-estado",
                    evento
                );
            } catch (Exception e) {
                // Si no hay usuarios conectados, no importa
                System.out.println("No se pudo enviar notificación WebSocket: " + e.getMessage());
            }
        }
    }
}

    @Scheduled(fixedRate = 300000) // 5 minutos
    public void verificarExpiradosProgramado() {
        verificarYActualizarExpirados();
    }

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
    }

    @Transactional
    public void cancelarIntercambio(Long intercambioId, String usuarioEmail) {
        Intercambio intercambio = intercambioRepository.findById(intercambioId)
                .orElseThrow(() -> new IntercambioNoEncontradoException(intercambioId));

        if (intercambio.getEstado() != EstadoIntercambio.PENDIENTE
                && intercambio.getEstado() != EstadoIntercambio.ACEPTADO) {
            throw new IntercambioNoAceptableException(intercambio.getEstado());
        }

        if (!intercambio.getPropietario().getEmail().equals(usuarioEmail)
                && !intercambio.getSolicitante().getEmail().equals(usuarioEmail)) {
            throw new IntercambioNoAutorizadoException();
        }

        if (intercambio.getEstado() == EstadoIntercambio.ACEPTADO) {
            chatRepository.delete(intercambio.getChat());
        }

        intercambio.setEstado(EstadoIntercambio.CANCELADO);
        intercambio.setFechaFin(LocalDateTime.now());

        intercambio.setChat(null);

        intercambioRepository.save(intercambio);
    }

    @Transactional
    public void concretarIntercambio(Long intercambioId, String usuarioEmail) {
        Intercambio intercambio = intercambioRepository.findById(intercambioId)
                .orElseThrow(() -> new IntercambioNoEncontradoException(intercambioId));

        if (!intercambio.getSolicitante().getEmail().equals(usuarioEmail)
                && !intercambio.getPropietario().getEmail().equals(usuarioEmail)) {
            throw new IntercambioNoAutorizadoException();
        }

        // Marcar que el usuario concreto el intercambio
        if (intercambio.getSolicitante().getEmail().equals(usuarioEmail)) {
            intercambio.setSolicitanteConcreto(true);
        } else {
            intercambio.setPropietarioConcreto(true);
        }

        // Si ambos concretaron, actualizar estado
        if (intercambio.isConcretado()) {
            intercambio.setEstado(EstadoIntercambio.CONCRETADO);
        }

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
