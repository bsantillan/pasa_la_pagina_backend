package com.example.pasa_la_pagina.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pasa_la_pagina.entities.Seccion;

public interface SeccionRepository extends JpaRepository<Seccion,Long>{
    
    Optional<Seccion> findByNombre(String nombre);

}
