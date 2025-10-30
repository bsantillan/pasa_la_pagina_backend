package com.example.pasa_la_pagina.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Prefijo para los topics a los que los clientes se van a suscribir
        config.enableSimpleBroker("/topic"); 
        // Prefijo para los mensajes enviados desde el cliente
        config.setApplicationDestinationPrefixes("/app"); 
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // URL de conexión del cliente
                .setAllowedOriginPatterns("*") // permitir frontend en diferentes orígenes
                .withSockJS(); // fallback si WebSocket no está disponible
    }
}
