package com.example.pasa_la_pagina.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pasa_la_pagina.entities.Genero;

public interface GeneroRepository extends JpaRepository<Genero, Long>{
    
    Optional<Genero> findByNombre(String nombre);

}
