package com.example.pasa_la_pagina.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pasa_la_pagina.entities.Usuario;
import com.example.pasa_la_pagina.entities.VerificationToken;
import com.example.pasa_la_pagina.entities.Enum.TokenType;
import com.example.pasa_la_pagina.exceptions.CodigoInvalidoException;
import com.example.pasa_la_pagina.exceptions.PasswordInvalidaException;
import com.example.pasa_la_pagina.repositories.UsuarioRepository;
import com.example.pasa_la_pagina.repositories.VerificationTokenRepository;
import com.example.pasa_la_pagina.utils.PasswordValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

        private final UsuarioRepository usuarioRepository;
        private final VerificationTokenRepository tokenRepository;
        private final PasswordEncoder passwordEncoder;
        private final SenGridService emailService;

        @Transactional
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

                emailService.sendResetPasswordEmail(
                                usuario.getEmail(),
                                Map.of("nombre", usuario.getNombre(), "codigo", token.getToken()));
        }

        @Transactional
        public void verificarEmail(String email, String codigo) {
                Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

                if (usuarioOpt.isEmpty()) {
                        return;
                }

                Usuario usuario = usuarioOpt.get();

                VerificationToken token = tokenRepository
                                .findByUsuarioAndType(usuario, TokenType.RESET_PASSWORD)
                                .orElseThrow(() -> new CodigoInvalidoException("Código invalido"));

                if (token.esExpirado()) {
                        throw new CodigoInvalidoException("Codigo inválido");
                }

                if (!token.esValido(codigo)) {
                        throw new CodigoInvalidoException("Código inválido");
                }

                tokenRepository.delete(token);
        }

        @Transactional
        public void reenviarVerificacion(String email) {
                Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

                if (usuarioOpt.isEmpty()) {
                        return;
                }

                Usuario usuario = usuarioOpt.get();

                tokenRepository.deleteByUsuarioAndType(usuario, TokenType.RESET_PASSWORD);

                enviarResetPassword(email);
        }

        public void resetPassword(String email, String nuevaPassword) {

                if (!PasswordValidator.esValida(nuevaPassword)) {
                        throw new PasswordInvalidaException();
                }
                Usuario usuario = usuarioRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                usuario.setPassword_hash(passwordEncoder.encode(nuevaPassword));
                usuarioRepository.save(usuario);
        }
}
