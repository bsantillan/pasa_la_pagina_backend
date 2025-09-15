package com.example.pasa_la_pagina.repositories;

import com.example.pasa_la_pagina.entities.Intercambio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IntercambioRepository extends JpaRepository<Intercambio, Long> {
    List<Intercambio> findBySolicitante_Id(Long usuarioId);
    List<Intercambio> findByPropietario_Id(Long usuarioId);
}
