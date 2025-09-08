package com.example.pasa_la_pagina.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.pasa_la_pagina.DTOs.requests.UpdateUsuarioRequest;
import com.example.pasa_la_pagina.DTOs.response.RecuperarUsuarioResponse;
import com.example.pasa_la_pagina.entities.Usuario;
import com.example.pasa_la_pagina.exceptions.UsuarioNoEncontradoException;
import com.example.pasa_la_pagina.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private RecuperarUsuarioResponse mapToResponse(Usuario usuario) {

        RecuperarUsuarioResponse response = new RecuperarUsuarioResponse();
        response.setId(usuario.getId());
        response.setNombre(usuario.getNombre());
        response.setApellido(usuario.getApellido());
        response.setEmail(usuario.getEmail());
        return response;

    }

    public RecuperarUsuarioResponse recuperarUsuario(Long id){
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNoEncontradoException("Usuario con ID " + id + " no encontrado"));
        return mapToResponse(usuario);
    }

    public List<RecuperarUsuarioResponse> recuperarTodosUsuarios(){
        List<RecuperarUsuarioResponse> listado = new ArrayList<>();
        List<Usuario> usuarios = usuarioRepository.findAll();
        for (Usuario usuario : usuarios) {
            listado.add(mapToResponse(usuario)); 
        }
        return listado;
    }

    public RecuperarUsuarioResponse recuperarUsuarioByEmail(String email){
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(()-> new UsuarioNoEncontradoException("Usuario con email " + email + " no encontrado"));
        return mapToResponse(usuario);
    }

    public RecuperarUsuarioResponse actualizarUsuario(String email, UpdateUsuarioRequest usuarioRequest){
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(()-> new UsuarioNoEncontradoException("Usuario con email " + email + " no encontrado"));
        usuario.setNombre(usuarioRequest.getNombre());
        usuario.setApellido(usuarioRequest.getApellido());
        usuarioRepository.save(usuario);
        return mapToResponse(usuario);
    }

}
