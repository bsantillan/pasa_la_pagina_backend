package com.example.pasa_la_pagina.services;

import com.example.pasa_la_pagina.DTOs.requests.CrearReseniaRequest;
import com.example.pasa_la_pagina.DTOs.response.ReseniaResponse;
import com.example.pasa_la_pagina.entities.Intercambio;
import com.example.pasa_la_pagina.entities.Resenia;
import com.example.pasa_la_pagina.entities.Usuario;
import com.example.pasa_la_pagina.repositories.IntercambioRepository;
import com.example.pasa_la_pagina.repositories.ReseniaRepository;
import com.example.pasa_la_pagina.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReseniaService {

    private final ReseniaRepository reseniaRepository;
    private final UsuarioRepository usuarioRepository;
    private final IntercambioRepository intercambioRepository;

    // 1️⃣ Crear una reseña
    public ReseniaResponse crearResenia(CrearReseniaRequest request) {
        Usuario autor = usuarioRepository.findById(request.getAutorId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Intercambio intercambio = intercambioRepository.findById(request.getIntercambioId())
                .orElseThrow(() -> new RuntimeException("Intercambio no encontrado"));

        Resenia resenia = Resenia.builder()
                .descripcion(request.getDescripcion())
                .valoracion(request.getValoracion())
                .autor(autor)
                .intercambio(intercambio)
                .build();

        reseniaRepository.save(resenia);
        return convertToResponse(resenia);
    }

    // 2️⃣ Reseñas recibidas por un usuario
    public List<ReseniaResponse> getReseniasRecibidas(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Resenia> resenias = reseniaRepository.findReseniasRelacionadasConUsuario(usuario);

        return resenias.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 3️⃣ Reseñas hechas por un usuario
    public List<ReseniaResponse> getReseniasHechas(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Resenia> resenias = reseniaRepository.findByAutor(usuario);

        return resenias.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 4️⃣ Reseñas por intercambio
    public List<ReseniaResponse> getReseniasPorIntercambio(Long intercambioId) {
        Intercambio intercambio = intercambioRepository.findById(intercambioId)
                .orElseThrow(() -> new RuntimeException("Intercambio no encontrado"));

        List<Resenia> resenias = reseniaRepository.findByIntercambio(intercambio);

        return resenias.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Método auxiliar para mapear Resenia -> DTO
    private ReseniaResponse convertToResponse(Resenia r) {
        return ReseniaResponse.builder()
                .id(r.getId())
                .descripcion(r.getDescripcion())
                .valoracion(r.getValoracion())
                .autorId(r.getAutor() != null ? r.getAutor().getId() : null)
                .autorNombre(r.getAutor() != null ? r.getAutor().getNombre() : "Anónimo")
                .intercambioId(r.getIntercambio().getId())
                .build();
    }
}
