package com.example.pasa_la_pagina.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pasa_la_pagina.entities.Autor;

public interface AutorRepository extends JpaRepository<Autor, Long>{
    
    Optional<Autor> findByNombre(String nombre);
}
