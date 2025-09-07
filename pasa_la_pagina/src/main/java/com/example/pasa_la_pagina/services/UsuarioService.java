package com.example.pasa_la_pagina.services;

import org.springframework.stereotype.Service;

import com.example.pasa_la_pagina.DTOs.response.RecuperarUsuarioResponse;
import com.example.pasa_la_pagina.entities.Usuario;
import com.example.pasa_la_pagina.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public RecuperarUsuarioResponse recuperarUsuario(Long id){
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        RecuperarUsuarioResponse response = new RecuperarUsuarioResponse();
        response.setId(usuario.getId());
        response.setNombre(usuario.getNombre());
        response.setApellido(usuario.getApellido());
        response.setEmail(usuario.getEmail());
        return response;
    }
}
