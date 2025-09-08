package com.example.pasa_la_pagina.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.pasa_la_pagina.entities.Libro;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    
}
