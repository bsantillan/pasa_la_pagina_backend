package com.example.pasa_la_pagina.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pasa_la_pagina.entities.Institucion;
import com.example.pasa_la_pagina.entities.Enum.NivelEducativo;

public interface InstitucionRepository extends JpaRepository<Institucion,Long>{
    
    Optional<Institucion> findByNombreAndNivelEducativo(String nombre, NivelEducativo nivel_educativo);
}
