package com.example.pasa_la_pagina.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pasa_la_pagina.entities.Materia;

public interface MateriaRepository extends JpaRepository<Materia,Long>{
    
    Optional<Materia> findByNombre(String nombre);

}
