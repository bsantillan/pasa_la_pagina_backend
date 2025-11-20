package com.example.pasa_la_pagina.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.pasa_la_pagina.entities.Notificacion;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    @Query("SELECT n FROM Notificacion n " +
            "WHERE n.receptor.id = :usuarioId")
    Page<Notificacion> findAllNotificaciones(Pageable pageable, @Param("usuarioId") Long usuarioId);
}
