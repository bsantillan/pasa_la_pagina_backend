package com.example.pasa_la_pagina.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.pasa_la_pagina.DTOs.GoogleUser;
import com.example.pasa_la_pagina.DTOs.requests.LoginRequest;
import com.example.pasa_la_pagina.DTOs.requests.RegisterRequest;
import com.example.pasa_la_pagina.DTOs.response.LoginResponse;
import com.example.pasa_la_pagina.DTOs.response.RegisterResponse;
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

    public RegisterResponse register(RegisterRequest reg_rq) {
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
        Usuario usuario_bd = usuarioRepository.save(usuario);

        RegisterResponse response = new RegisterResponse();
        response.setId(usuario_bd.getId());
        response.setEmail(usuario_bd.getEmail());
        response.setApellido(usuario_bd.getApellido());
        response.setNombre(usuario_bd.getNombre());
        response.setAccessToken(jwtUtil.generateToken(usuario.getEmail()));
        response.setRefreshToken(jwtUtil.createRefreshToken(usuario_bd).getToken());
        return response;
    }

    public LoginResponse login(LoginRequest log_rq) {
        Usuario usuario = usuarioRepository.findByEmail(log_rq.getEmail())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (!passwordEncoder.matches(log_rq.getPassword(), usuario.getPassword_hash())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        LoginResponse response = new LoginResponse();
        response.setAccessToken(jwtUtil.generateToken(usuario.getEmail()));
        response.setRefreshToken(jwtUtil.createRefreshToken(usuario).getToken());
        return response;
    }

    public LoginResponse loginWithGoogle(String idToken) {
        GoogleUser googleUser = googleTokenVerifier.verify(idToken);
        Usuario usuario = usuarioRepository.findByEmail(googleUser.getEmail())
                .orElseGet(() -> usuarioRepository.save(Usuario.builder()
                        .email(googleUser.getEmail())
                        .nombre(googleUser.getFirstName())
                        .apellido(googleUser.getLastName())
                        .provider("google")
                        .build()));
        LoginResponse response = new LoginResponse();
        response.setAccessToken(jwtUtil.generateToken(usuario.getEmail()));
        response.setRefreshToken(jwtUtil.createRefreshToken(usuario).getToken());
        return response;
    }

    public String refreshToken(String token){

        if (!jwtUtil.validateRefreshToken(token)) {
            throw new RuntimeException("Token invalido");
        }

        Usuario usuario = jwtUtil.getUsuarioFromToken(token);
        return (jwtUtil.generateToken(usuario.getEmail()));
    }
}
