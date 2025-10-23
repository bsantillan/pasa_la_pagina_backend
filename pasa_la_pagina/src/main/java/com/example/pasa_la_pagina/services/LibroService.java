package com.example.pasa_la_pagina.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.pasa_la_pagina.DTOs.response.LibroResponse;
import com.example.pasa_la_pagina.entities.Foto;
import com.example.pasa_la_pagina.entities.Libro;
import com.example.pasa_la_pagina.repositories.LibroRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class LibroService {

    private final LibroRepository libroRepository;

    private LibroResponse mapLibroResponse(Libro libro) {
        LibroResponse response = new LibroResponse();
        response.setId(libro.getId());
        response.setTitulo(libro.getTitulo());
        response.setIdioma(libro.getIdioma().getNombre());
        response.setIsbn(libro.getIsbn());
        response.setEditorial(libro.getEditorial().getNombre());
        response.setAutor(libro.getAutor().getNombre());
        response.setUrl_fotos(libro.getFotos().stream().map(Foto::getUrl).toList());
        return response;
    }

    public List<LibroResponse> buscarLibros(String isbn) {
        return libroRepository.findByIsbn(isbn).stream()
            .map(this::mapLibroResponse)
            .toList();
    }
}
