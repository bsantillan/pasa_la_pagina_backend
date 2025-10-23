package com.example.pasa_la_pagina.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pasa_la_pagina.entities.Libro;

public interface LibroRepository extends JpaRepository<Libro, Integer> {

    List<Libro> findByIsbn(String isbn);
}