package com.example.pasa_la_pagina.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.pasa_la_pagina.entities.Usuario;
import com.example.pasa_la_pagina.entities.VerificationToken;
import com.example.pasa_la_pagina.entities.Enum.TokenType;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByUsuarioAndType(Usuario usuario, TokenType type);

    void deleteByUsuarioAndType(Usuario usuario, TokenType type);

}
