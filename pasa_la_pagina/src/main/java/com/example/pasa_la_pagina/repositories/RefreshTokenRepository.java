package com.example.pasa_la_pagina.repositories;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pasa_la_pagina.entities.RefreshToken;
import com.example.pasa_la_pagina.entities.Usuario;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUsuario(Usuario usuario);
    void deleteAllByExpiryDateBefore(Instant expiryDate);

}
