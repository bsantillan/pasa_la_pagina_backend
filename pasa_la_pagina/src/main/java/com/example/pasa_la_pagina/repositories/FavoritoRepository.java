package com.example.pasa_la_pagina.repositories;

import com.example.pasa_la_pagina.entities.Favorito;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    void deleteByUsuarioIdAndPublicacionId(Long usuarioId, Long publicacionId);

    Page<Favorito> findByUsuarioId(Long usuarioId, Pageable pageable);

    boolean existsByUsuarioIdAndPublicacionId(Long usuarioId, Long publicacionId);
}
