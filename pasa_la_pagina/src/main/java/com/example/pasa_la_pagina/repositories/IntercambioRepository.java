package com.example.pasa_la_pagina.repositories;

import com.example.pasa_la_pagina.entities.Intercambio;
import com.example.pasa_la_pagina.entities.Enum.EstadoIntercambio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IntercambioRepository extends JpaRepository<Intercambio, Long> {
    Page<Intercambio> findBySolicitante_Id(Pageable pageable, Long usuarioId);

    Page<Intercambio> findByPropietario_Id(Pageable pageable, Long usuarioId);

    Optional<Intercambio> findByPublicacion_IdAndSolicitante_Id(Long publicacionId, Long solicitanteId);

    @Query("""
            SELECT DISTINCT i FROM Intercambio i
            JOIN FETCH i.publicacion p
            JOIN FETCH p.material m
            JOIN FETCH i.solicitante sol
            JOIN FETCH i.propietario prop
            WHERE
                (i.propietario.id = :usuarioId AND (i.estado ='ACEPTADO' OR i.estado ='CONCRETADO')) OR (i.solicitante.id = :usuarioId AND (i.estado ='ACEPTADO' OR i.estado ='CONCRETADO') )
                AND (:estados IS NULL OR i.estado IN :estados)
                AND (:fechaInicio IS NULL OR i.fechaInicio >= :fechaInicio)
                AND (:tituloPublicacion IS NULL OR LOWER(m.titulo) LIKE LOWER(CONCAT('%', :tituloPublicacion, '%')))
                AND (
                    :usuarioNombre IS NULL
                    OR LOWER(CONCAT(sol.nombre, ' ', sol.apellido)) LIKE LOWER(CONCAT('%', :usuarioNombre, '%'))
                )
            """)
    Page<Intercambio> buscarIntercambiosConFiltros(
            @Param("usuarioId") Long usuarioId,
            @Param("estados") List<EstadoIntercambio> estados,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("tituloPublicacion") String tituloPublicacion,
            @Param("usuarioNombre") String usuarioNombre,
            Pageable pageable);

    @Query("SELECT i FROM Intercambio i " +
            "WHERE i.propietario.id = :usuarioId ")
    Page<Intercambio> buscarTodosIntercambios(Pageable pageable, @Param("usuarioId") Long usuarioId);

}
