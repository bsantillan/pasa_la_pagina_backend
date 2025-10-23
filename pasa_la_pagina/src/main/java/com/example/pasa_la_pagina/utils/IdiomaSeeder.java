package com.example.pasa_la_pagina.utils;

import java.util.Locale;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.pasa_la_pagina.entities.Idioma;
import com.example.pasa_la_pagina.repositories.IdiomaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IdiomaSeeder implements CommandLineRunner{

    private final IdiomaRepository idiomaRepository;

@Override
    public void run(String... args) {
        Locale displayLocale = Locale.of("es"); // nombres en español

        // Recorremos todos los locales disponibles
        for (Locale locale : Locale.getAvailableLocales()) {
            String codigo = locale.getLanguage();

            // Solo códigos ISO de 2 letras válidos
            if (!codigo.isEmpty() && codigo.length() == 2) {
                String nombreEnEspanol = locale.getDisplayLanguage(displayLocale);

                // Buscar por nombre, si no existe lo crea
                idiomaRepository.findByNombre(nombreEnEspanol)
                        .orElseGet(() -> idiomaRepository.save(
                            Idioma.builder().nombre(nombreEnEspanol).codigo(codigo).build()
                        ));
            }
        }
    }
}
