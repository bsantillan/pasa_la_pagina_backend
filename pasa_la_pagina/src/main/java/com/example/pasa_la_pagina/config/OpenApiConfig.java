package com.example.pasa_la_pagina.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pasarLaPaginaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pasa la Pagina API")
                        .description("Documentacion de los endpoints REST de Pasa la Pagina.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo Backend")
                                .email("backend@pasalapagina.local")));
    }
}
