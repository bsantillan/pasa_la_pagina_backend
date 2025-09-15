package com.example.pasa_la_pagina.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pasa_la_pagina.entities.Mensaje;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    Page<Mensaje> findByChatIdOrderByFechaInicioDesc(Long chatId, Pageable pageable);
}

