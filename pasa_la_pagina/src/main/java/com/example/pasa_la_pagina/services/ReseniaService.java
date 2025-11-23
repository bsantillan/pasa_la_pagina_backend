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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReseniaService {

        private final ReseniaRepository reseniaRepository;
        private final UsuarioRepository usuarioRepository;
        private final IntercambioRepository intercambioRepository;

        // 1️⃣ Crear una reseña
        public void crearResenia(CrearReseniaRequest request, String reseniadorEmail) {
                Usuario reseniador = usuarioRepository.findByEmail(reseniadorEmail)
                                .orElseThrow(() -> new RuntimeException("Reseniador no encontrado"));

                Usuario reseniado = usuarioRepository.findById(request.getReseniadoId())
                                .orElseThrow(() -> new RuntimeException("Reseniado no encontrado"));

                Intercambio intercambio = intercambioRepository.findById(request.getIntercambioId())
                                .orElseThrow(() -> new RuntimeException("Intercambio no encontrado"));

                Resenia resenia = Resenia.builder()
                                .descripcion(request.getDescripcion())
                                .valoracion(request.getValoracion())
                                .reseniado(reseniado)
                                .reseniador(reseniador)
                                .intercambio(intercambio)
                                .fecha(LocalDateTime.now())
                                .build();

                reseniaRepository.save(resenia);
        }

        // 2️⃣ Reseñas recibidas por un usuario
        public List<ReseniaResponse> getReseniasRecibidas(String usuarioEmail) {
                Usuario usuario = usuarioRepository.findByEmail(usuarioEmail)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                List<Resenia> resenias = reseniaRepository.findByReseniadoId(usuario.getId());

                return resenias.stream()
                                .map((r) -> convertToResponse(r, true))
                                .collect(Collectors.toList());
        }

        // 3️⃣ Reseñas hechas por un usuario
        public List<ReseniaResponse> getReseniasHechas(String usuarioEmail) {
                Usuario usuario = usuarioRepository.findByEmail(usuarioEmail)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                List<Resenia> resenias = reseniaRepository.findByReseniadorId(usuario.getId());

                return resenias.stream()
                                .map((r) -> convertToResponse(r, false))
                                .collect(Collectors.toList());
        }

        // Método auxiliar para mapear Resenia -> DTO
        private ReseniaResponse convertToResponse(Resenia r, Boolean reseniador) {
                Usuario usuario = reseniador ? r.getReseniador() : r.getReseniado();
                return ReseniaResponse.builder()
                                .id(r.getId())
                                .descripcion(r.getDescripcion())
                                .valoracion(r.getValoracion())
                                .usuarioId(usuario != null ? usuario.getId() : null)
                                .usuarioNombre(usuario != null ? usuario.getApellido() + " " + usuario.getNombre()
                                                : "Anónimo")
                                .intercambioId(r.getIntercambio().getId())
                                .fecha(r.getFecha())
                                .build();
        }
}
