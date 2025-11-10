package com.example.pasa_la_pagina.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.pasa_la_pagina.DTOs.GoogleUser;
import com.example.pasa_la_pagina.DTOs.requests.LoginRequest;
import com.example.pasa_la_pagina.DTOs.requests.RegisterRequest;
import com.example.pasa_la_pagina.DTOs.response.LoginResponse;
import com.example.pasa_la_pagina.DTOs.response.RegisterResponse;
import com.example.pasa_la_pagina.entities.Usuario;
import com.example.pasa_la_pagina.exceptions.CredencialesInvalidasException;
import com.example.pasa_la_pagina.exceptions.EmailEnUsoException;
import com.example.pasa_la_pagina.repositories.RefreshTokenRepository;
import com.example.pasa_la_pagina.repositories.UsuarioRepository;
import com.example.pasa_la_pagina.utils.GoogleTokenVerifier;
import com.example.pasa_la_pagina.utils.JWTUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final GoogleTokenVerifier googleTokenVerifier;

    public RegisterResponse register(RegisterRequest reg_rq) {
        if (usuarioRepository.existsByEmail(reg_rq.getEmail())) {
            throw new EmailEnUsoException("El email esta en uso");
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
                .orElseThrow(() -> new CredencialesInvalidasException("Email o contraseña incorrectos"));

        if (!passwordEncoder.matches(log_rq.getPassword(), usuario.getPassword_hash())) {
            throw new CredencialesInvalidasException("Email o contraseña incorrectos");
        }
        refreshTokenRepository.deleteByUsuario(usuario);

        LoginResponse response = new LoginResponse();
        response.setAccessToken(jwtUtil.generateToken(usuario.getEmail()));
        response.setRefreshToken(jwtUtil.createRefreshToken(usuario).getToken());
        response.setId(usuario.getId());
        response.setApellido(usuario.getApellido());
        response.setNombre(usuario.getNombre());
        response.setEmail(usuario.getEmail());

        return response;
    }

    public LoginResponse loginWithGoogle(String idToken) {
        try {
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
        } catch (Exception ex) {
            throw new CredencialesInvalidasException("Token de google invalido");
        }
    }

    public String refreshToken(String token) {

        if (!jwtUtil.validateRefreshToken(token)) {
            throw new CredencialesInvalidasException("Token invalido");
        }

        Usuario usuario = jwtUtil.getUsuarioFromToken(token);
        return (jwtUtil.generateToken(usuario.getEmail()));
    }

    public void logout(String refreshToken) {

        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new CredencialesInvalidasException("Token inválido");
        }
        Usuario usuario = jwtUtil.getUsuarioFromToken(refreshToken);
        jwtUtil.deleteRefreshToken(usuario);
    }

}
