package com.example.pasa_la_pagina.repositories;

import com.example.pasa_la_pagina.entities.Intercambio;
import com.example.pasa_la_pagina.entities.Resenia;
import com.example.pasa_la_pagina.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReseniaRepository extends JpaRepository<Resenia, Long> {

    // Reseñas recibidas (por un usuario propietario o solicitante)
    @Query("SELECT r FROM Resenia r WHERE r.intercambio.propietario = :usuario OR r.intercambio.solicitante = :usuario")
    List<Resenia> findReseniasRelacionadasConUsuario(Usuario usuario);

    List<Resenia> findByReseniadoId(Long reseniadoId);

    List<Resenia> findByReseniadorId(Long reseniadorId);

    // Reseñas de un intercambio específico
    List<Resenia> findByIntercambio(Intercambio intercambio);
}
