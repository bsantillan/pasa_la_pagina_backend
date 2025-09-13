package com.example.pasa_la_pagina.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pasa_la_pagina.entities.Carrera;

public interface CarreraRepository extends JpaRepository<Carrera,Long>{
    
    Optional<Carrera> findByNombre(String nombre);
}
