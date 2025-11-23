package com.example.pasa_la_pagina;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PasaLaPaginaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PasaLaPaginaApplication.class, args);
	}

}
