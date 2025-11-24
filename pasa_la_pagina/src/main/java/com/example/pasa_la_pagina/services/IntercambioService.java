package com.example.pasa_la_pagina.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
import com.example.pasa_la_pagina.DTOs.response.IntercambioResenaResponse;
import com.example.pasa_la_pagina.DTOs.response.NotificacionUpdateResponse;
import com.example.pasa_la_pagina.DTOs.response.PageRecuperarResponse;
import com.example.pasa_la_pagina.DTOs.response.RecuperarIntercambioResponse;
import com.example.pasa_la_pagina.entities.Chat;
import com.example.pasa_la_pagina.entities.Intercambio;
import com.example.pasa_la_pagina.entities.Notificacion;
import com.example.pasa_la_pagina.entities.Publicacion;
import com.example.pasa_la_pagina.entities.Usuario;
import com.example.pasa_la_pagina.entities.Enum.EstadoIntercambio;
import com.example.pasa_la_pagina.entities.Enum.TipoNotificacion;
import com.example.pasa_la_pagina.entities.Enum.TipoUpdateNotificacion;
import com.example.pasa_la_pagina.exceptions.IntercambioInvalidoException;
import com.example.pasa_la_pagina.exceptions.IntercambioNoAceptableException;
import com.example.pasa_la_pagina.exceptions.IntercambioNoAutorizadoException;
import com.example.pasa_la_pagina.exceptions.IntercambioNoEncontradoException;
import com.example.pasa_la_pagina.exceptions.PublicacionNoEncontradaException;
import com.example.pasa_la_pagina.exceptions.UsuarioNoEncontradoException;
import com.example.pasa_la_pagina.repositories.ChatRepository;
import com.example.pasa_la_pagina.repositories.IntercambioRepository;
import com.example.pasa_la_pagina.repositories.NotificacionRepository;
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
    private final NotificacionRepository notificacionRepository;
    private final SimpMessagingTemplate messagingTemplate;

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
                intercambio.setEstado(EstadoIntercambio.CANCELADO);
                intercambio.setFechaFin(LocalDateTime.now());
                intercambio.setCodigoValidacion(null);
                intercambio.setCodigoExpiracion(null);
                
                intercambioRepository.save(intercambio);
                
                try {
                IntercambioEstadoEvento evento = IntercambioEstadoEvento.builder()
                        .intercambioId(intercambio.getId())
                        .estado(EstadoIntercambio.CANCELADO)
                        .puedeDejarResena(true)
                        .mensaje("Intercambio cancelado por expiración del código de validación")
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

    @Transactional(readOnly = true)
    public List<IntercambioResenaResponse> getIntercambiosReseñables(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + emailUsuario));

        List<Intercambio> intercambios = intercambioRepository.findBySolicitanteOrPropietarioWithPublicacion(usuario.getId());

        return intercambios.stream()
                .map(i -> mapToIntercambioResenaResponse(i, usuario))
                .collect(Collectors.toList());
    }

private IntercambioResenaResponse mapToIntercambioResenaResponse(Intercambio intercambio, Usuario usuario) {
    String rol = intercambio.getRolUsuario(usuario);
    Usuario otroUsuario = rol.equals("Solicitante") ? intercambio.getPropietario() : intercambio.getSolicitante();

    return IntercambioResenaResponse.builder()
            .id(intercambio.getId())
            .tituloPublicacion(intercambio.getPublicacion().getMaterial().getTitulo())
            .nombre(otroUsuario.getNombre())
            .apellido(otroUsuario.getApellido())
            .usuarioId(otroUsuario.getId())
            .usuarioEmail(otroUsuario.getEmail())
            .estado(intercambio.getEstado())
            .fechaInicio(intercambio.getFechaInicio().toString())
            .esReseñable(intercambio.isReseñable(usuario))
            .rolUsuario(rol)
            .build();
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

        Notificacion notiificacion = notificacionService.crearNotificacion(
                TipoNotificacion.SOLICITUD_INTERCAMBIO,
                intercambio,
                null,
                null,
                propietario,
                solicitante);

        notificacionService.enviarNotificacionAUsuario(notiificacion);
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

        Notificacion notiificacion = notificacionService.crearNotificacion(
                TipoNotificacion.INTERCAMBIO_ACEPTADO,
                intercambio,
                null,
                null,
                intercambio.getSolicitante(),
                intercambio.getPropietario());

        notificacionService.enviarNotificacionAUsuario(notiificacion);

        intercambioRepository.save(intercambio);
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

        Usuario receptor;
        if (intercambio.getPropietario().getId().equals(usuario.getId())) {
            receptor = intercambio.getSolicitante();
        } else {
            receptor = intercambio.getPropietario();
        }

        Notificacion notiificacion = notificacionService.crearNotificacion(
                TipoNotificacion.INTERCAMBIO_CANCELADO,
                intercambio,
                null,
                null,
                receptor,
                usuario);

        notificacionService.enviarNotificacionAUsuario(notiificacion);

        intercambioRepository.save(intercambio);
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

        Usuario receptor;
        if (intercambio.getPropietario().getId().equals(usuario.getId())) {
            receptor = intercambio.getSolicitante();
        } else {
            receptor = intercambio.getPropietario();
        }

        Notificacion notiificacion = notificacionService.crearNotificacion(
                TipoNotificacion.INTERCAMBIO_RECHAZADO,
                intercambio,
                null,
                null,
                receptor,
                usuario);

        notificacionService.enviarNotificacionAUsuario(notiificacion);

        intercambioRepository.save(intercambio);
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

        Notificacion notiificacion = notificacionService.crearNotificacion(
                TipoNotificacion.INTERCAMBIO_CONCRETADO,
                intercambio,
                null,
                null,
                receptor,
                usuario);

        notificacionService.enviarNotificacionAUsuario(notiificacion);

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

        intercambios.forEach(intercambio -> notificacionRepository.deleteByReceptorIdAndIntercambioId(usuario.getId(),
                intercambio.getId()));

        messagingTemplate.convertAndSend(
                "/topic/notificaciones/" + usuario.getId(),
                new NotificacionUpdateResponse(TipoUpdateNotificacion.ACTUALIZAR_TODO, null));

        return mapToPageResponse(intercambios.map(i -> mapToResponseRecuperarIntercambio(i, usuario.getId())));
    }
}
