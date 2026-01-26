package com.example.pasa_la_pagina.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.pasa_la_pagina.entities.Usuario;
import com.example.pasa_la_pagina.entities.VerificationToken;
import com.example.pasa_la_pagina.entities.Enum.TokenType;
import com.example.pasa_la_pagina.repositories.UsuarioRepository;
import com.example.pasa_la_pagina.repositories.VerificationTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UsuarioRepository usuarioRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final SenGridService emailService;

    public void enviarResetPassword(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        tokenRepository.deleteByUsuarioAndType(usuario, TokenType.RESET_PASSWORD);

        VerificationToken token = VerificationToken.builder()
                .usuario(usuario)
                .token(VerificationToken.generarCodigo())
                .type(TokenType.RESET_PASSWORD)
                .fechaExpiracion(Instant.now().plus(15, ChronoUnit.MINUTES))
                .build();

        tokenRepository.save(token);

        emailService.sendEmail(
                usuario.getEmail(),
                "Recuperar contraseña para Pasá la Página",
                "Tu código para restablecer la contraseña es: " + token.getToken());
    }

    public void resetPassword(String email, String codigo, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        VerificationToken token = tokenRepository
                .findByUsuarioAndType(usuario, TokenType.RESET_PASSWORD)
                .orElseThrow(() -> new RuntimeException("Código inválido"));

        if (token.esExpirado()) {
            throw new RuntimeException("Token expirado");
        }

        if (!token.esValido(codigo)) {
            throw new RuntimeException("Código inválido o expirado");
        }

        usuario.setPassword_hash(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);

        tokenRepository.delete(token);
    }
}
