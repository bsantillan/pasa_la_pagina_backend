package com.example.pasa_la_pagina.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.pasa_la_pagina.DTOs.requests.PublicacionApunteRequest;
import com.example.pasa_la_pagina.DTOs.requests.PublicacionLibroRequest;
import com.example.pasa_la_pagina.DTOs.response.PublicacionLibroResponse;
import com.example.pasa_la_pagina.entities.Apunte;
import com.example.pasa_la_pagina.entities.Autor;
import com.example.pasa_la_pagina.entities.Editorial;
import com.example.pasa_la_pagina.entities.Foto;
import com.example.pasa_la_pagina.entities.Genero;
import com.example.pasa_la_pagina.entities.Institucion;
import com.example.pasa_la_pagina.entities.Libro;
import com.example.pasa_la_pagina.entities.Materia;
import com.example.pasa_la_pagina.entities.Publicacion;
import com.example.pasa_la_pagina.entities.Seccion;
import com.example.pasa_la_pagina.entities.Usuario;
import com.example.pasa_la_pagina.entities.Enum.NivelEducativo;
import com.example.pasa_la_pagina.entities.Enum.TipoOferta;
import com.example.pasa_la_pagina.repositories.AutorRepository;
import com.example.pasa_la_pagina.repositories.EditorialRepository;
import com.example.pasa_la_pagina.repositories.GeneroRepository;
import com.example.pasa_la_pagina.repositories.InstitucionRepository;
import com.example.pasa_la_pagina.repositories.MateriaRepository;
import com.example.pasa_la_pagina.repositories.PublicacionRepository;
import com.example.pasa_la_pagina.repositories.SeccionRepository;
import com.example.pasa_la_pagina.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublicacionService {

    private final PublicacionRepository publicacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final EditorialRepository editorialRepository;
    private final MateriaRepository materiaRepository;
    private final InstitucionRepository institucionRepository;
    private final SeccionRepository seccionRepository;
    private final GeneroRepository generoRepository;
    private final AutorRepository autorRepository;

    private PublicacionLibroResponse mapToResponsePublicacionLibro(Publicacion publicacion) {
        PublicacionLibroResponse response = new PublicacionLibroResponse();
        if (publicacion.getMaterial() instanceof Libro libro) {
            response.setId(publicacion.getId());
            response.setLatitud(publicacion.getLatitud());
            response.setLongitud(publicacion.getLongitud());
            response.setPrecio(publicacion.getPrecio());
            response.setTipo_oferta(publicacion.getTipo_oferta());
            response.setDisponible(publicacion.isDisponible());
            response.setTitulo(libro.getTitulo());
            response.setDigital(publicacion.isDigital());
            response.setDescripcion(libro.getDescripcion());
            response.setNuevo(libro.isNuevo());
            response.setIdioma(libro.getIdioma());
            response.setCantidad(libro.getCantidad());
            response.setEditorial(libro.getEditorial().getNombre());
            response.setAutor(libro.getAutor().getNombre());
            response.setGenero(libro.getGenero().getNombre());
            response.setUrl_fotos(libro.getFotos().stream().map(Foto::getUrl).toList());
        } else {
            throw new RuntimeException("El material no es un libro");
        }
        return response;
    }

    public PublicacionLibroResponse nuevaPublicacionLibro(PublicacionLibroRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId()).get();
        Editorial editorial = recuperarEditorial(request.getEditorial());
        Genero genero = recuperarGenero(request.getGenero());
        Autor autor = recuperarAutor(request.getAutor());

        Libro libro = Libro.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .nuevo(request.isNuevo())
                .idioma(request.getIdioma())
                .isbn(request.getIsbn())
                .cantidad(request.getCantidad())
                .editorial(editorial)
                .genero(genero)
                .autor(autor)
                .fotos(request.getFotos_url().stream().map(url -> Foto.builder().url(url).build()).toList())
                .build();

        Publicacion publicacion = Publicacion.builder()
                .fecha_creacion(LocalDateTime.now())
                .precio(calcularPrecio(request.getTipo_oferta(), request.getPrecio()))
                .digital(request.isDigital())
                .latitud(request.getLatitud())
                .longitud(request.getLongitud())
                .tipo_oferta(request.getTipo_oferta())
                .disponible(true)
                .material(libro)
                .usuario(usuario)
                .build();

        publicacionRepository.save(publicacion);
        return mapToResponsePublicacionLibro(publicacion);
    }

    public PublicacionLibroResponse nuevaPublicacionApunte(PublicacionApunteRequest request) {
        if(request.getNivel_educativo()==NivelEducativo.Superior && request.getCarrera()==null){
            throw new IllegalArgumentException("La carrera es obligatoria si el nivel educativo es SUPERIOR");
        }
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId()).get();
        Materia materia = recuperarMateria(request.getMateria());
        Institucion institucion = recuperarInstitucion(request.getInstitucion(), request.getNivel_educativo());
        Seccion seccion = recuperarSeccion(request.getSeccion());

        Apunte apunte = Apunte.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .nuevo(request.isNuevo())
                .idioma(request.getIdioma())
                .cantidad(request.getCantidad())
                .seccion(seccion)
                .materia(materia)
                .institucion(institucion)
                .fotos(request.getFotos_url().stream().map(url -> Foto.builder().url(url).build()).toList())
                .build();

        Publicacion publicacion = Publicacion.builder()
                .fecha_creacion(LocalDateTime.now())
                .precio(calcularPrecio(request.getTipo_oferta(), request.getPrecio()))
                .digital(request.isDigital())
                .latitud(request.getLatitud())
                .longitud(request.getLongitud())
                .tipo_oferta(request.getTipo_oferta())
                .disponible(true)
                .material(apunte)
                .usuario(usuario)
                .build();

        publicacionRepository.save(publicacion);
        return mapToResponsePublicacionLibro(publicacion);
    }

    public Double calcularPrecio(TipoOferta tipo_oferta, Double precio) {
        switch (tipo_oferta) {
            case TipoOferta.Venta:
                return precio;
            default:
                return (double) 0;
        }
    }

    public Editorial recuperarEditorial(String editorial) {
        return editorialRepository.findByNombre(editorial)
                .orElse(editorialRepository.save(Editorial.builder().nombre(editorial).build()));
    }

    public Genero recuperarGenero(String genero) {
        return generoRepository.findByNombre(genero)
                .orElse(generoRepository.save(Genero.builder().nombre(genero).build()));
    }

    public Autor recuperarAutor(String autor) {
        return autorRepository.findByNombre(autor)
                .orElse(autorRepository.save(Autor.builder().nombre(autor).build()));
    }

    public Materia recuperarMateria(String materia) {
        return materiaRepository.findByNombre(materia)
                .orElse(materiaRepository.save(Materia.builder().nombre(materia).build()));
    }

    public Institucion recuperarInstitucion(String institucion, NivelEducativo nivel_educativo) {
        return institucionRepository.findByNombreAndNivelEducativo(institucion, nivel_educativo)
                .orElse(institucionRepository
                        .save(Institucion.builder().nombre(institucion).nivel_educativo(nivel_educativo).build()));
    }

    public Seccion recuperarSeccion(String seccion) {
        return seccionRepository.findByNombre(seccion)
                .orElse(seccionRepository.save(Seccion.builder().nombre(seccion).build()));
    }
}
