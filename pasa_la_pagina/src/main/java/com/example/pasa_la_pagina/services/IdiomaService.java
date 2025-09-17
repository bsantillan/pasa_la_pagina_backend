package com.example.pasa_la_pagina.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.pasa_la_pagina.DTOs.response.IdiomaResponse;
import com.example.pasa_la_pagina.entities.Idioma;
import com.example.pasa_la_pagina.repositories.IdiomaRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class IdiomaService {
    
    private final IdiomaRepository idiomaRepository;

    private IdiomaResponse mapToResponseIdioma(Idioma idioma) {
        IdiomaResponse response = new IdiomaResponse();
        response.setNombre(idioma.getNombre());
        return response;
    }

    public List<IdiomaResponse> findAll(){
        return idiomaRepository.findAll()
            .stream()
            .map(this::mapToResponseIdioma)
            .collect(Collectors.toList());
    }
}
