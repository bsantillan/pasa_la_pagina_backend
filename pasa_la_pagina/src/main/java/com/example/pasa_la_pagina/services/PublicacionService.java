package com.example.pasa_la_pagina.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.pasa_la_pagina.DTOs.requests.PublicacionApunteRequest;
import com.example.pasa_la_pagina.DTOs.requests.PublicacionLibroRequest;
import com.example.pasa_la_pagina.DTOs.response.RecuperarPublicacionResponse;
import com.example.pasa_la_pagina.entities.Apunte;
import com.example.pasa_la_pagina.entities.Autor;
import com.example.pasa_la_pagina.entities.Carrera;
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
import com.example.pasa_la_pagina.entities.Enum.TipoMaterial;
import com.example.pasa_la_pagina.entities.Enum.TipoOferta;
import com.example.pasa_la_pagina.repositories.AutorRepository;
import com.example.pasa_la_pagina.repositories.CarreraRepository;
import com.example.pasa_la_pagina.repositories.EditorialRepository;
import com.example.pasa_la_pagina.repositories.GeneroRepository;
import com.example.pasa_la_pagina.repositories.InstitucionRepository;
import com.example.pasa_la_pagina.repositories.MateriaRepository;
import com.example.pasa_la_pagina.repositories.PublicacionRepository;
import com.example.pasa_la_pagina.repositories.SeccionRepository;
import com.example.pasa_la_pagina.repositories.UsuarioRepository;

import jakarta.transaction.Transactional;
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
    private final CarreraRepository carreraRepository;
    private final GeneroRepository generoRepository;
    private final AutorRepository autorRepository;

    private RecuperarPublicacionResponse mapToResponseRecuperarPublicacion(Publicacion publicacion) {
        RecuperarPublicacionResponse response = new RecuperarPublicacionResponse();
        response.setId(publicacion.getId());
        response.setLatitud(publicacion.getLatitud());
        response.setLongitud(publicacion.getLongitud());
        response.setPrecio(publicacion.getPrecio());
        response.setTipo_oferta(publicacion.getTipo_oferta());
        response.setDisponible(publicacion.getDisponible());
        response.setUsuario_id(publicacion.getUsuario().getId());
        if (publicacion.getMaterial() instanceof Libro libro) {
            response.setTipo_material(TipoMaterial.Libro);
            response.setTitulo(libro.getTitulo());
            response.setDescripcion(libro.getDescripcion());
            response.setDigital(libro.getDigital());
            response.setNuevo(libro.getNuevo());
            response.setIdioma(libro.getIdioma());
            response.setCantidad(libro.getCantidad());
            response.setEditorial(libro.getEditorial().getNombre());
            response.setAutor(libro.getAutor().getNombre());
            response.setGenero(libro.getGenero().getNombre());
            response.setUrl_fotos(libro.getFotos().stream().map(Foto::getUrl).toList());
        } else {
            if (publicacion.getMaterial() instanceof Apunte apunte) {
                response.setTipo_material(TipoMaterial.Apunte);
                response.setTitulo(apunte.getTitulo());
                response.setDescripcion(apunte.getDescripcion());
                response.setDigital(apunte.getDigital());
                response.setNuevo(apunte.getNuevo());
                response.setIdioma(apunte.getIdioma());
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
        return response;
    }

    public RecuperarPublicacionResponse nuevaPublicacionLibro(PublicacionLibroRequest request) {

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId()).get();
        Editorial editorial = recuperarEditorial(request.getEditorial());
        Genero genero = recuperarGenero(request.getGenero());
        Autor autor = recuperarAutor(request.getAutor());

        Libro libro = Libro.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .digital(request.getDigital())
                .nuevo(request.getNuevo())
                .idioma(request.getIdioma())
                .isbn(request.getIsbn())
                .cantidad(request.getCantidad())
                .editorial(editorial)
                .genero(genero)
                .autor(autor)
                .build();
        libro.setFotos(
                request.getFotos_url().stream().map(url -> Foto.builder().url(url).material(libro).build()).toList());

        Publicacion publicacion = Publicacion.builder()
                .fecha_creacion(LocalDateTime.now())
                .precio(calcularPrecio(request.getTipo_oferta(), request.getPrecio()))
                .latitud(request.getLatitud())
                .longitud(request.getLongitud())
                .tipo_oferta(request.getTipo_oferta())
                .disponible(true)
                .material(libro)
                .usuario(usuario)
                .build();

        publicacionRepository.save(publicacion);
        return mapToResponseRecuperarPublicacion(publicacion);
    }

    public RecuperarPublicacionResponse nuevaPublicacionApunte(PublicacionApunteRequest request) {

        if (request.getNivel_educativo() == NivelEducativo.Superior && request.getCarrera() == null) {
            throw new IllegalArgumentException("La carrera es obligatoria si el nivel educativo es Superior");
        }
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId()).get();
        Institucion institucion = recuperarInstitucion(request.getInstitucion(), request.getNivel_educativo());
        Seccion seccion = recuperarSeccion(request.getSeccion());
        Materia materia = recuperarMateria(request.getMateria());
        ;
        Carrera carrera = null;
        if (request.getNivel_educativo() == NivelEducativo.Superior) {
            carrera = recuperarCarrera(request.getCarrera());
        }

        Apunte apunte = Apunte.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .digital(request.getDigital())
                .nuevo(request.getNuevo())
                .idioma(request.getIdioma())
                .cantidad(request.getCantidad())
                .anio_elaboracion(request.getAnio_elaboracion())
                .cantidad_paginas(request.getCantidad_paginas())
                .seccion(seccion)
                .materia(materia)
                .carrera(carrera)
                .institucion(institucion)
                .build();
        apunte.setFotos(
                request.getFotos_url().stream().map(url -> Foto.builder().url(url).material(apunte).build()).toList());

        Publicacion publicacion = Publicacion.builder()
                .fecha_creacion(LocalDateTime.now())
                .precio(calcularPrecio(request.getTipo_oferta(), request.getPrecio()))
                .latitud(request.getLatitud())
                .longitud(request.getLongitud())
                .tipo_oferta(request.getTipo_oferta())
                .disponible(true)
                .material(apunte)
                .usuario(usuario)
                .build();

        publicacionRepository.save(publicacion);
        return mapToResponseRecuperarPublicacion(publicacion);
    }

    public Double calcularPrecio(TipoOferta tipo_oferta, Double precio) {
        switch (tipo_oferta) {
            case TipoOferta.Venta:
                return precio;
            default:
                return (double) 0;
        }
    }

    @Transactional
    public Editorial recuperarEditorial(String editorial) {
        return editorialRepository.findByNombre(editorial)
                .orElseGet(() -> editorialRepository.save(Editorial.builder().nombre(editorial).build()));
    }

    public Genero recuperarGenero(String genero) {
        return generoRepository.findByNombre(genero)
                .orElseGet(() -> generoRepository.save(Genero.builder().nombre(genero).build()));
    }

    public Autor recuperarAutor(String autor) {
        return autorRepository.findByNombre(autor)
                .orElseGet(() -> autorRepository.save(Autor.builder().nombre(autor).build()));
    }

    public Materia recuperarMateria(String materia) {
        return materiaRepository.findByNombre(materia)
                .orElseGet(() -> materiaRepository.save(Materia.builder().nombre(materia).build()));
    }

    public Institucion recuperarInstitucion(String institucion, NivelEducativo nivel_educativo) {
        return institucionRepository.findByNombreAndNivelEducativo(institucion, nivel_educativo)
                .orElseGet(() -> institucionRepository
                        .save(Institucion.builder().nombre(institucion).nivelEducativo(nivel_educativo).build()));
    }

    public Seccion recuperarSeccion(String seccion) {
        return seccionRepository.findByNombre(seccion)
                .orElseGet(() -> seccionRepository.save(Seccion.builder().nombre(seccion).build()));
    }

    public Carrera recuperarCarrera(String carrera) {
        return carreraRepository.findByNombre(carrera)
                .orElseGet(() -> carreraRepository.save(Carrera.builder().nombre(carrera).build()));
    }
}
