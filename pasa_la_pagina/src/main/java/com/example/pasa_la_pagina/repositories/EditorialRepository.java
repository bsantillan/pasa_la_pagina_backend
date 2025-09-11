package com.example.pasa_la_pagina.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pasa_la_pagina.entities.Editorial;

public interface EditorialRepository extends JpaRepository<Editorial, Long> {
    
    Optional<Editorial> findByNombre(String nombre);

}
