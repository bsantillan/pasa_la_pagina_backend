package com.example.pasa_la_pagina.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pasa_la_pagina.entities.Usuario;
import com.example.pasa_la_pagina.entities.VerificationToken;
import com.example.pasa_la_pagina.entities.Enum.TokenType;
import com.example.pasa_la_pagina.exceptions.CodigoInvalidoException;
import com.example.pasa_la_pagina.exceptions.CuentaVerificadaException;
import com.example.pasa_la_pagina.repositories.UsuarioRepository;
import com.example.pasa_la_pagina.repositories.VerificationTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final UsuarioRepository usuarioRepository;
    private final VerificationTokenRepository tokenRepository;
    private final SenGridService emailService;

    @Transactional
    public void enviarTokenVerificacion(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        tokenRepository.deleteByUsuarioAndType(usuario, TokenType.VERIFY_EMAIL);

        VerificationToken token = VerificationToken.builder()
                .usuario(usuario)
                .token(VerificationToken.generarCodigo())
                .type(TokenType.VERIFY_EMAIL)
                .fechaExpiracion(Instant.now().plus(1, ChronoUnit.DAYS))
                .build();

        tokenRepository.save(token);

        emailService.sendVerificationEmail(
                usuario.getEmail(),
                Map.of("nombre", usuario.getNombre(), "codigo", token.getToken()));
    }

    @Transactional
    public void verificarEmail(String email, String codigo) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getEmail_verificado()) {
            throw new CuentaVerificadaException();
        }

        VerificationToken token = tokenRepository
                .findByUsuarioAndType(usuario, TokenType.VERIFY_EMAIL)
                .orElseThrow(() -> new CodigoInvalidoException("Código invalido"));

        if (token.esExpirado()) {
            throw new CodigoInvalidoException("Codigo inválido");
        }

        if (!token.esValido(codigo)) {
            throw new CodigoInvalidoException("Código inválido");
        }

        usuario.setEmail_verificado(true);
        usuario.setEnabled(true);
        usuarioRepository.save(usuario);

        tokenRepository.delete(token);
    }

    @Transactional
    public void reenviarVerificacion(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getEmail_verificado()) {
            throw new CuentaVerificadaException();
        }

        tokenRepository.deleteByUsuarioAndType(usuario, TokenType.VERIFY_EMAIL);

        enviarTokenVerificacion(email);
    }
}
