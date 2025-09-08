package com.example.pasa_la_pagina.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.pasa_la_pagina.entities.Material;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByUsuarioId(Long usuarioId);
}
