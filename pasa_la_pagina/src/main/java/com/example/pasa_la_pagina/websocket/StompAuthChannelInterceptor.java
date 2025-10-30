package com.example.pasa_la_pagina.websocket;

import java.security.Principal;
import java.util.List;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import com.example.pasa_la_pagina.entities.Usuario;
import com.example.pasa_la_pagina.repositories.UsuarioRepository;
import com.example.pasa_la_pagina.utils.JWTUtil;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JWTUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = first(accessor.getNativeHeader("Authorization"));
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new MessageDeliveryException("Authorization header missing or invalid");
            }
            String token = authHeader.substring(7);
            String email;
            try {
                email = jwtUtil.recuperarMail(token);
            } catch (Exception e) {
                throw new MessageDeliveryException("Invalid JWT");
            }
            Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
            if (usuario == null) {
                throw new MessageDeliveryException("User not found");
            }
            Principal principal = new UsernamePasswordAuthenticationToken(email, null, List.of());
            accessor.setUser(principal);
        }

        return message;
    }

    private String first(List<String> headers) {
        return (headers == null || headers.isEmpty()) ? null : headers.get(0);
    }
}

