package com.example.pasa_la_pagina.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pasa_la_pagina.DTOs.response.PageRecuperarResponse;
import com.example.pasa_la_pagina.DTOs.response.RecuperarPublicacionResponse;
import com.example.pasa_la_pagina.entities.Apunte;
import com.example.pasa_la_pagina.entities.Favorito;
import com.example.pasa_la_pagina.entities.Foto;
import com.example.pasa_la_pagina.entities.Libro;
import com.example.pasa_la_pagina.entities.Publicacion;
import com.example.pasa_la_pagina.entities.Usuario;
import com.example.pasa_la_pagina.entities.Enum.TipoMaterial;
import com.example.pasa_la_pagina.exceptions.UsuarioNoEncontradoException;
import com.example.pasa_la_pagina.repositories.FavoritoRepository;
import com.example.pasa_la_pagina.repositories.PublicacionRepository;
import com.example.pasa_la_pagina.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PublicacionRepository publicacionRepository;

    private PageRecuperarResponse mapToResponsePageRecuperarPublicacion(
            Page<RecuperarPublicacionResponse> page_publicaciones) {
        PageRecuperarResponse response = new PageRecuperarResponse();
        response.setContent(page_publicaciones.getContent());
        response.setSize(page_publicaciones.getSize());
        response.setTotalElements(page_publicaciones.getTotalElements());
        response.setTotalPages(page_publicaciones.getTotalPages());
        return response;
    }

    private RecuperarPublicacionResponse mapToResponseRecuperarPublicacion(Publicacion publicacion, Long usuarioId) {
        RecuperarPublicacionResponse response = new RecuperarPublicacionResponse();
        response.setId(publicacion.getId());
        response.setLatitud(publicacion.getLatitud());
        response.setLongitud(publicacion.getLongitud());
        response.setPrecio(publicacion.getPrecio());
        response.setTipo_oferta(publicacion.getTipo_oferta());
        response.setDisponible(publicacion.getDisponible());
        response.setUsuario_id(publicacion.getUsuario().getId());
        response.setUsuario_nombre(publicacion.getUsuario().getNombre());
        response.setUsuario_apellido(publicacion.getUsuario().getApellido());
        if (publicacion.getMaterial() instanceof Libro libro) {
            response.setTipo_material(TipoMaterial.Libro);
            response.setTitulo(libro.getTitulo());
            response.setUrl(libro.getUrl());
            response.setDescripcion(libro.getDescripcion());
            response.setDigital(libro.getDigital());
            response.setNuevo(libro.getNuevo());
            response.setIdioma(libro.getIdioma().getNombre());
            response.setCantidad(libro.getCantidad());
            response.setIsbn(libro.getIsbn());
            response.setEditorial(libro.getEditorial().getNombre());
            response.setAutor(libro.getAutor().getNombre());
            response.setGenero(libro.getGenero().getNombre());
            response.setUrl_fotos(libro.getFotos().stream().map(Foto::getUrl).toList());
        } else {
            if (publicacion.getMaterial() instanceof Apunte apunte) {
                response.setTipo_material(TipoMaterial.Apunte);
                response.setTitulo(apunte.getTitulo());
                response.setUrl(apunte.getUrl());
                response.setDescripcion(apunte.getDescripcion());
                response.setDigital(apunte.getDigital());
                response.setNuevo(apunte.getNuevo());
                response.setIdioma(apunte.getIdioma().getNombre());
                response.setCantidad(apunte.getCantidad());
                response.setUrl_fotos(apunte.getFotos().stream().map(Foto::getUrl).toList());
                response.setCantidad_paginas(apunte.getCantidad_paginas());
                response.setAnio_elaboracion(apunte.getAnio_elaboracion());
                response.setMateria(apunte.getMateria().getNombre());
                response.setInstitucion(apunte.getInstitucion().getNombre());
                response.setNivel_educativo(apunte.getInstitucion().getNivelEducativo());
                response.setSeccion(apunte.getSeccion().getNombre());
                response.setCarrera(apunte.getCarrera() != null ? apunte.getCarrera().getNombre() : null);

            } else {
                throw new RuntimeException("El material no es un libro ni un apunte");
            }
        }
        boolean esFavorito = favoritoRepository.existsByUsuarioIdAndPublicacionId(usuarioId, publicacion.getId());
        response.setFavorito(esFavorito);
        return response;
    }

    @Transactional
    public void nuevoFavorito(Long publicacionId, String userEmail) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario no encontrado: " + userEmail));

        Publicacion publicacion = publicacionRepository.findById(publicacionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontro una publicacion con el id: " + publicacionId));

        Favorito favorito = Favorito.builder()
                .publicacion(publicacion)
                .usuario(usuario)
                .build();

        favoritoRepository.save(favorito);
    }

    @Transactional
    public void eliminarFavorito(Long publicacionId, String userEmail) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario no encontrado: " + userEmail));

        Publicacion publicacion = publicacionRepository.findById(publicacionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontro una publicacion con el id: " + publicacionId));

        favoritoRepository.deleteByUsuarioIdAndPublicacionId(usuario.getId(), publicacion.getId());
    }

    @Transactional
    public PageRecuperarResponse recuperarFavoritos(Pageable pageable, String userEmail) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario no encontrado: " + userEmail));

        Page<Favorito> favoritosPage = favoritoRepository.findByUsuarioId(usuario.getId(), pageable);

        // Mapear cada Favorito a su Publicacion y luego a RecuperarPublicacionResponse
        Page<RecuperarPublicacionResponse> responsePage = favoritosPage
                .map(favorito -> mapToResponseRecuperarPublicacion(favorito.getPublicacion(), usuario.getId()));

        return mapToResponsePageRecuperarPublicacion(responsePage);
    }
}
