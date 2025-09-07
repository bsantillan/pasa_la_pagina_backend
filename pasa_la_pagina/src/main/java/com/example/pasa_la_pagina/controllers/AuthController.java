package com.example.pasa_la_pagina.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pasa_la_pagina.DTOs.requests.GoogleLoginRequest;
import com.example.pasa_la_pagina.DTOs.requests.LoginRequest;
import com.example.pasa_la_pagina.DTOs.requests.RegisterRequest;
import com.example.pasa_la_pagina.DTOs.response.LoginResponse;
import com.example.pasa_la_pagina.DTOs.response.RegisterResponse;
import com.example.pasa_la_pagina.services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.created(null)
                            .body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    
    @PostMapping("/google")
    public ResponseEntity<LoginResponse> loginWithGoogle(@RequestBody GoogleLoginRequest request) {
        return ResponseEntity.ok(authService.loginWithGoogle(request.getIdToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(Map.of("accessToken", authService.refreshToken(request.get("refreshToken"))));
    }
}
