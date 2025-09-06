package com.example.pasa_la_pagina.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.pasa_la_pagina.DTOs.GoogleUser;
import com.example.pasa_la_pagina.DTOs.requests.LoginRequest;
import com.example.pasa_la_pagina.DTOs.requests.RegisterRequest;
import com.example.pasa_la_pagina.entities.Usuario;
import com.example.pasa_la_pagina.repositories.UsuarioRepository;
import com.example.pasa_la_pagina.utils.GoogleTokenVerifier;
import com.example.pasa_la_pagina.utils.JWTUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final GoogleTokenVerifier googleTokenVerifier;

    public String register(RegisterRequest reg_rq) {
        if (usuarioRepository.existsByEmail(reg_rq.getEmail())){
            throw new RuntimeException("El usuario ya existe");
        }
        Usuario usuario = Usuario.builder()
                        .email(reg_rq.getEmail())
                        .nombre(reg_rq.getNombre())
                        .apellido(reg_rq.getApellido())
                        .password_hash(passwordEncoder.encode(reg_rq.getPassword()))
                        .provider("local")
                        .build();
        usuarioRepository.save(usuario);
        return jwtUtil.generateToken(usuario.getEmail());
    }

    public String login(LoginRequest log_rq) {
        Usuario usuario = usuarioRepository.findByEmail(log_rq.getEmail())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (!passwordEncoder.matches(log_rq.getPassword(), usuario.getPassword_hash())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        return jwtUtil.generateToken(usuario.getEmail());
    }

    public String loginWithGoogle(String idToken) {
        GoogleUser googleUser = googleTokenVerifier.verify(idToken);
        Usuario usuario = usuarioRepository.findByEmail(googleUser.getEmail())
                .orElseGet(() -> usuarioRepository.save(Usuario.builder()
                        .email(googleUser.getEmail())
                        .nombre(googleUser.getFirstName())
                        .apellido(googleUser.getLastName())
                        .provider("google")
                        .build()));
        return jwtUtil.generateToken(usuario.getEmail());
    }
}
