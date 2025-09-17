package com.example.pasa_la_pagina.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pasa_la_pagina.entities.Idioma;

public interface IdiomaRepository extends JpaRepository<Idioma,Long>{
    
    Optional<Idioma> findByNombre(String nombre);
}
