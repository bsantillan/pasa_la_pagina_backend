package com.example.pasa_la_pagina.websocket;

import org.springframework.context.annotation.Configuration; // marca la clase como bean de configuración de Spring
import org.springframework.messaging.simp.config.MessageBrokerRegistry; // configuración del broker STOMP/Message broker
import org.springframework.beans.factory.annotation.Autowired; // inyección de dependencias por constructor
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker; // habilita STOMP sobre WebSocket
import org.springframework.web.socket.config.annotation.StompEndpointRegistry; // registro de endpoints de handshake (SockJS/WebSocket)
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer; // interfaz para configurar el broker WebSocket
import org.springframework.messaging.simp.config.ChannelRegistration; // para configurar interceptores de canal (inbound/outbound)

/*
    Clase de configuración para habilitar y personalizar STOMP sobre WebSocket.

    Qué hace en términos generales:
    - Expone un endpoint de handshake para que los clientes se conecten (/ws).
    - Define prefijos para rutas que vienen del cliente hacia los @MessageMapping (@app).
    - Define prefijo para destinos dirigidos a usuarios (/user).
    - Activa un broker simple en memoria para reenviar mensajes a suscriptores (/topic, /queue).
    - Registra un interceptor para manejar autenticación/autoría de frames STOMP entrantes.
*/
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Interceptor propio que normalmente valida el token JWT en las tramas
    // CONNECT/SEND/SUBSCRIBE
    // y asocia un Principal a la sesión WebSocket. Se inyecta por constructor.
    private final StompAuthChannelInterceptor stompAuthChannelInterceptor;

    @Autowired
    public WebSocketConfig(StompAuthChannelInterceptor stompAuthChannelInterceptor) {
        this.stompAuthChannelInterceptor = stompAuthChannelInterceptor;
    }

    /*
     * registerStompEndpoints:
     * - Define el punto de handshake que el cliente usaría para abrir la conexión
     * WebSocket.
     * - setAllowedOriginPatterns: controla CORS para el handshake (orígenes
     * permitidos).
     * - withSockJS(): habilita fallback SockJS (útil en desarrollo o navegadores
     * sin WebSocket).
     * Ejemplo de cliente: conectar a ws://HOST/ws (o usar SockJS client).
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                // limitar orígenes a tus frontends; ajustar a producción cuando sea necesario
                .setAllowedOriginPatterns("http://localhost:8082", "http://localhost:8081")
                .withSockJS(); // habilita fallback SockJS (no obligatorio en producción)
    }

    /*
     * configureMessageBroker:
     * - setApplicationDestinationPrefixes("/app"):
     * Indica que mensajes enviados por el cliente con prefijo /app serán
     * enrutados a métodos del servidor anotados con @MessageMapping.
     * Ej: cliente -> /app/chats/send -> @MessageMapping("/chats/send")
     * - setUserDestinationPrefix("/user"):
     * Prefijo lógico para destinos "por usuario" (entregas privadas).
     * Permite usar messagingTemplate.convertAndSendToUser(username, "/queue/..",
     * payload)
     * y que el cliente se suscriba a /user/queue/...
     * - enableSimpleBroker("/topic", "/queue"):
     * Activa un broker en memoria que administra suscripciones y entrega
     * mensajes a clientes suscritos a destinos que comienzan por /topic o /queue.
     * /topic típicamente para broadcast, /queue para colas privadas.
     * Notas:
     * - enableSimpleBroker es suficiente para desarrollo y despliegues pequeños.
     * - Para alta escala usar un broker externo (RabbitMQ, ActiveMQ, Redis).
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
        registry.enableSimpleBroker("/topic", "/queue");
    }

    /*
     * configureClientInboundChannel:
     * - Registra interceptores que procesan tramas entrantes desde clientes
     * (CONNECT, SEND, SUBSCRIBE...).
     * - Un StompAuthChannelInterceptor típico:
     * Extrae token JWT del encabezado CONNECT o de la sesión.
     * Valida el token y construye un Principal (username/email).
     * Inserta el Principal en el header/atributos de la sesión para que
     * convertAndSendToUser y la seguridad funcionen correctamente.
     * - Importante: sin un Principal correcto, convertAndSendToUser no entregará al
     * usuario esperado.
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompAuthChannelInterceptor);
    }
}