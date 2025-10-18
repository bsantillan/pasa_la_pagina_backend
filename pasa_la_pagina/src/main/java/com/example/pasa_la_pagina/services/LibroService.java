package com.example.pasa_la_pagina.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.pasa_la_pagina.entities.Libro;
import com.example.pasa_la_pagina.repositories.LibroRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class LibroService {

    private final LibroRepository libroRepository;

    public List<Libro> buscarLibros(String isbn) {
        return libroRepository.findByIsbn(isbn);
    }
}
