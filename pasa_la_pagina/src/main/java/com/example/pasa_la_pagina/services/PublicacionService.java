package com.example.pasa_la_pagina.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.pasa_la_pagina.DTOs.requests.BuscarPublicacionRequest;
import com.example.pasa_la_pagina.DTOs.requests.DeletePublicacionRequest;
import com.example.pasa_la_pagina.DTOs.requests.PublicacionApunteRequest;
import com.example.pasa_la_pagina.DTOs.requests.PublicacionLibroRequest;
import com.example.pasa_la_pagina.DTOs.requests.UpdatePublicacionRequest;
import com.example.pasa_la_pagina.DTOs.response.PageRecuperarResponse;
import com.example.pasa_la_pagina.DTOs.response.RecuperarPublicacionResponse;
import com.example.pasa_la_pagina.entities.Apunte;
import com.example.pasa_la_pagina.entities.Autor;
import com.example.pasa_la_pagina.entities.Carrera;
import com.example.pasa_la_pagina.entities.Editorial;
import com.example.pasa_la_pagina.entities.Foto;
import com.example.pasa_la_pagina.entities.Genero;
import com.example.pasa_la_pagina.entities.Idioma;
import com.example.pasa_la_pagina.entities.Institucion;
import com.example.pasa_la_pagina.entities.Libro;
import com.example.pasa_la_pagina.entities.Materia;
import com.example.pasa_la_pagina.entities.Publicacion;
import com.example.pasa_la_pagina.entities.Seccion;
import com.example.pasa_la_pagina.entities.Usuario;
import com.example.pasa_la_pagina.entities.Enum.NivelEducativo;
import com.example.pasa_la_pagina.entities.Enum.TipoMaterial;
import com.example.pasa_la_pagina.entities.Enum.TipoOferta;
import com.example.pasa_la_pagina.exceptions.UsuarioNoEncontradoException;
import com.example.pasa_la_pagina.repositories.AutorRepository;
import com.example.pasa_la_pagina.repositories.CarreraRepository;
import com.example.pasa_la_pagina.repositories.EditorialRepository;
import com.example.pasa_la_pagina.repositories.GeneroRepository;
import com.example.pasa_la_pagina.repositories.IdiomaRepository;
import com.example.pasa_la_pagina.repositories.InstitucionRepository;
import com.example.pasa_la_pagina.repositories.MateriaRepository;
import com.example.pasa_la_pagina.repositories.PublicacionRepository;
import com.example.pasa_la_pagina.repositories.SeccionRepository;
import com.example.pasa_la_pagina.repositories.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
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
    private final IdiomaRepository idiomaRepository;

    private RecuperarPublicacionResponse mapToResponseRecuperarPublicacion(Publicacion publicacion) {
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
        return response;
    }

    private PageRecuperarResponse mapToResponsePageRecuperarPublicacion(
            Page<RecuperarPublicacionResponse> page_publicaciones) {
        PageRecuperarResponse response = new PageRecuperarResponse();
        response.setContent(page_publicaciones.getContent());
        response.setSize(page_publicaciones.getSize());
        response.setTotalElements(page_publicaciones.getTotalElements());
        response.setTotalPages(page_publicaciones.getTotalPages());
        return response;
    }

    public RecuperarPublicacionResponse nuevaPublicacionLibro(PublicacionLibroRequest request) {
        if (request.getTipo_oferta() != TipoOferta.Venta && request.getPrecio() != null) {
            throw new IllegalArgumentException("El precio solo puede ser especificado si el tipo de oferta es 'Venta'");
        }
        if (request.getTipo_oferta() == TipoOferta.Venta && request.getPrecio() == null) {
            throw new IllegalArgumentException("El precio es obligatorio si el tipo de oferta es 'Venta'");
        }
        if (!request.getDigital() && request.getCantidad() == null) {
            throw new IllegalArgumentException("La cantidad es obligatoria si el material es formato 'Digital'");
        }
        if (request.getDigital() && request.getUrl() == null) {
            throw new IllegalArgumentException(
                    "La url del material es obligatoria si el material es formato 'Digital'");
        }

        Idioma idioma = recuperarIdioma(request.getIdioma());
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId()).get();
        Editorial editorial = recuperarEditorial(request.getEditorial());
        Genero genero = recuperarGenero(request.getGenero());
        Autor autor = recuperarAutor(request.getAutor());

        Libro libro = Libro.builder()
                .titulo(request.getTitulo())
                .url(request.getUrl())
                .descripcion(request.getDescripcion())
                .digital(request.getDigital())
                .nuevo(request.getNuevo())
                .idioma(idioma)
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
                .precio(request.getPrecio())
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
        if (request.getTipo_oferta() != TipoOferta.Venta && request.getPrecio() != null) {
            throw new IllegalArgumentException("El precio solo puede ser especificado si el tipo de oferta es 'Venta'");
        }
        if (request.getTipo_oferta() == TipoOferta.Venta && request.getPrecio() == null) {
            throw new IllegalArgumentException("El precio es obligatorio si el tipo de oferta es 'Venta'");
        }
        if (!request.getDigital() && request.getCantidad() == null) {
            throw new IllegalArgumentException("La cantidad es obligatoria si el material es formato 'Digital'");
        }
        if (request.getDigital() && request.getUrl() == null) {
            throw new IllegalArgumentException(
                    "La url del material es obligatoria si el material es formato 'Digital'");
        }

        Idioma idioma = recuperarIdioma(request.getIdioma());
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId()).get();
        Institucion institucion = recuperarInstitucion(request.getInstitucion(), request.getNivel_educativo());
        Seccion seccion = recuperarSeccion(request.getSeccion());
        Materia materia = recuperarMateria(request.getMateria());
        Carrera carrera = null;
        if (request.getNivel_educativo() == NivelEducativo.Superior) {
            carrera = recuperarCarrera(request.getCarrera());
        }

        Apunte apunte = Apunte.builder()
                .titulo(request.getTitulo())
                .url(request.getUrl())
                .descripcion(request.getDescripcion())
                .digital(request.getDigital())
                .nuevo(request.getNuevo())
                .idioma(idioma)
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
                .precio(request.getPrecio())
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

    public RecuperarPublicacionResponse actualizarPublicacion(UpdatePublicacionRequest request) {
        Publicacion publicacion = publicacionRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontro una publicacion con el id: " + request.getId()));
        if (publicacion.getUsuario().getId() != request.getUsuarioId()) {
            throw new IllegalArgumentException("La publicacion no es del usuario con id: " + request.getUsuarioId());
        }
        if (request.getTipo_material() == TipoMaterial.Apunte && request.getNivel_educativo() != NivelEducativo.Superior
                && request.getCarrera() == null) {
            throw new IllegalArgumentException("La carrera es obligatoria si el nivel educativo es Superior");
        }
        if (request.getUrl_fotos() == null || request.getUrl_fotos().isEmpty()) {
            throw new IllegalArgumentException("Debe haber alguna imagen en la publicacion");
        }
        if (request.getTipo_oferta() != TipoOferta.Venta && request.getPrecio() != null) {
            throw new IllegalArgumentException("El precio solo puede ser especificado si el tipo de oferta es 'Venta'");
        }
        if (request.getTipo_oferta() == TipoOferta.Venta && request.getPrecio() == null) {
            throw new IllegalArgumentException("El precio es obligatorio si el tipo de oferta es 'Venta'");
        }
        if (!request.getDigital() && request.getCantidad() == null) {
            throw new IllegalArgumentException("La cantidad es obligatoria si el material es formato 'Digital'");
        }
        if (request.getDigital() && request.getUrl() == null) {
            throw new IllegalArgumentException(
                    "La url del material es obligatoria si el material es formato 'Digital'");
        }

        if (request.getLatitud() != null)
            publicacion.setLatitud(request.getLatitud());
        if (request.getLongitud() != null)
            publicacion.setLongitud(request.getLongitud());
        if (request.getTipo_oferta() != null)
            publicacion.setTipo_oferta(request.getTipo_oferta());
        if (request.getPrecio() != null)
            publicacion.setPrecio(request.getPrecio());
        if (request.getTitulo() != null)
            publicacion.getMaterial().setTitulo(request.getTitulo());
        if (request.getUrl() != null)
            publicacion.getMaterial().setUrl(request.getUrl());
        if (request.getDescripcion() != null)
            publicacion.getMaterial().setDescripcion(request.getDescripcion());
        if (request.getNuevo() != null)
            publicacion.getMaterial().setNuevo(request.getNuevo());
        if (request.getDigital() != null)
            publicacion.getMaterial().setDigital(request.getDigital());
        if (request.getIdioma() != null)
            publicacion.getMaterial().setIdioma(recuperarIdioma(request.getIdioma()));
        if (request.getCantidad() != null)
            publicacion.getMaterial().setCantidad(request.getCantidad());
        if (request.getUrl_fotos() != null) {
            List<Foto> fotosActuales = publicacion.getMaterial().getFotos();

            fotosActuales.removeIf(foto -> request.getUrl_fotos()
                    .stream()
                    .noneMatch(url -> url.trim().equalsIgnoreCase(foto.getUrl().trim())));

            // Obtener las URLs que ya existen
            List<String> urlsExistentes = fotosActuales.stream()
                    .map(Foto::getUrl)
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .toList();

            // Agregar fotos nuevas
            List<Foto> nuevasFotos = request.getUrl_fotos().stream()
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .filter(url -> !urlsExistentes.contains(url))
                    .map(url -> Foto.builder()
                            .url(url)
                            .material(publicacion.getMaterial())
                            .build())
                    .toList();
            publicacion.getMaterial().getFotos().addAll(nuevasFotos);
        }

        if (publicacion.getMaterial() instanceof Libro libro) {
            if (request.getIsbn() != null)
                libro.setIsbn(request.getIsbn());
            if (request.getEditorial() != null)
                libro.setEditorial(recuperarEditorial(request.getEditorial()));
            if (request.getGenero() != null)
                libro.setGenero(recuperarGenero(request.getGenero()));
            if (request.getAutor() != null)
                libro.setAutor(recuperarAutor(request.getAutor()));
        }

        if (publicacion.getMaterial() instanceof Apunte apunte) {
            if (request.getCantidad_paginas() != null)
                apunte.setCantidad_paginas(request.getCantidad_paginas());
            if (request.getAnio_elaboracion() != null)
                apunte.setAnio_elaboracion(request.getAnio_elaboracion());
            if (request.getMateria() != null)
                apunte.setMateria(recuperarMateria(request.getMateria()));
            if (request.getInstitucion() != null)
                apunte.setInstitucion(recuperarInstitucion(request.getInstitucion(),
                        request.getNivel_educativo() != null ? request.getNivel_educativo()
                                : apunte.getInstitucion().getNivelEducativo()));
            if (request.getSeccion() != null)
                apunte.setSeccion(recuperarSeccion(request.getSeccion()));
            if (request.getNivel_educativo() != NivelEducativo.Superior) {
                apunte.setCarrera(null);
            }
            if (request.getCarrera() != null
                    && apunte.getInstitucion().getNivelEducativo() == NivelEducativo.Superior) {
                apunte.setCarrera(recuperarCarrera(request.getCarrera()));
            }
        }

        publicacionRepository.save(publicacion);

        return mapToResponseRecuperarPublicacion(publicacion);
    }

    public PageRecuperarResponse buscarPublicaciones(BuscarPublicacionRequest request, Pageable pageable,
            String userEmail) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + userEmail));
        if (request == null)
            return mapToResponsePageRecuperarPublicacion(
                    publicacionRepository.findAllDisponibles(pageable, usuario.getId())
                            .map(this::mapToResponseRecuperarPublicacion));

        if ((request.getPrecio_minimo() != null || request.getPrecio_maximo() != null)
                && request.getTipos_oferta() == null) {
            throw new IllegalArgumentException(
                    "Si se especifica un precio mínimo o maximo, debe incluirse el tipo de oferta 'Venta'");
        }
        if (request.getPrecio_minimo() != null && !request.getTipos_oferta().contains(TipoOferta.Venta)) {
            throw new IllegalArgumentException(
                    "Si se especifica un precio minimo, debe incluirse el tipo de oferta 'Venta'");
        }
        if (request.getPrecio_maximo() != null && !request.getTipos_oferta().contains(TipoOferta.Venta)) {
            throw new IllegalArgumentException(
                    "Si se especifica un precio maximo, debe incluirse el tipo de oferta 'Venta'");
        }
        List<Publicacion> publicaciones = new ArrayList<>();
        List<String> idiomas = (request.getIdiomas() == null || request.getIdiomas().isEmpty()) ? null
                : request.getIdiomas();
        List<TipoOferta> tipos_ofertas = (request.getTipos_oferta() == null || request.getTipos_oferta().isEmpty())
                ? null
                : request.getTipos_oferta();
        String query = request.getQuery();
        Boolean nuevo = request.getNuevo();
        Boolean digital = request.getDigital();
        Double precio_minimo = request.getPrecio_minimo();
        Double precio_maximo = request.getPrecio_maximo();
        Double usuarioLat = request.getUsuario_latitud();
        Double usuarioLon = request.getUsuario_longitud();
        Double distanciaMax = request.getDistancia_maxima();
        if (distanciaMax != null && (usuarioLat == null || usuarioLon == null)) {
            throw new IllegalArgumentException(
                    "Para filtrar por distancia, se deben proporcionar coordenadas del usuario");
        }
        List<NivelEducativo> niveles_educativos = (request.getNiveles_educativos() == null
                || request.getNiveles_educativos().isEmpty()) ? null : request.getNiveles_educativos();
        if (request.getTipos_material() == null || request.getTipos_material().isEmpty()) {
            publicaciones.addAll(publicacionRepository.buscarPorLibroDisponibles(
                    query, nuevo, digital, idiomas, tipos_ofertas, precio_minimo, precio_maximo, usuarioLat, usuarioLon,
                    distanciaMax, usuario.getId()));
            publicaciones.addAll(publicacionRepository.buscarPorApunteDisponibles(
                    query, nuevo, digital, idiomas, tipos_ofertas, niveles_educativos, precio_minimo, precio_maximo,
                    usuarioLat, usuarioLon, distanciaMax, usuario.getId()));
        } else {
            if (request.getTipos_material().contains(TipoMaterial.Libro)) {
                publicaciones.addAll(publicacionRepository.buscarPorLibroDisponibles(query, nuevo, digital, idiomas,
                        tipos_ofertas, precio_minimo, precio_maximo, usuarioLat, usuarioLon, distanciaMax,
                        usuario.getId()));
            }
            if (request.getTipos_material().contains(TipoMaterial.Apunte)) {
                publicaciones.addAll(publicacionRepository.buscarPorApunteDisponibles(query, nuevo, digital, idiomas,
                        tipos_ofertas, niveles_educativos, precio_minimo, precio_maximo, usuarioLat, usuarioLon,
                        distanciaMax, usuario.getId()));
            }
        }
        // Paginar
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), publicaciones.size());
        List<RecuperarPublicacionResponse> contenido = publicaciones.subList(start, end)
                .stream()
                .map(this::mapToResponseRecuperarPublicacion)
                .toList();
        Page<RecuperarPublicacionResponse> response = new PageImpl<>(contenido, pageable, publicaciones.size());
        return mapToResponsePageRecuperarPublicacion(response);
    }

    public PageRecuperarResponse recuperarPublicaciones(Pageable pageable, Double usuario_longitud,
            Double usuario_latitud, String userEmail) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + userEmail));
        Page<RecuperarPublicacionResponse> publicaciones = publicacionRepository
                .findAllDisponiblesOrderByDistance(usuario_latitud, usuario_longitud, pageable, usuario.getId())
                .map(this::mapToResponseRecuperarPublicacion);
        PageRecuperarResponse response = new PageRecuperarResponse();
        response.setContent(publicaciones.getContent());
        response.setSize(publicaciones.getSize());
        response.setTotalElements(publicaciones.getTotalElements());
        response.setTotalPages(publicaciones.getTotalPages());

        return response;
    }

    public RecuperarPublicacionResponse recuperarPublicacionById(Long id) {
        Publicacion publicacion = publicacionRepository.findDisponibleById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro una publicacion con el id: " + id));
        return mapToResponseRecuperarPublicacion(publicacion);
    }

    public void eliminarPublicacionById(DeletePublicacionRequest request, String userEmail) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario no encontrado: " + userEmail));
        Publicacion publicacion = publicacionRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontro una publicacion con el id: " + request.getId()));
        if (publicacion.getUsuario().getId() != usuario.getId()) {
            throw new IllegalArgumentException("La publicacion no es del usuario con id: " + usuario.getId());
        }
        publicacion.setDisponible(false);
        publicacionRepository.save(publicacion);
    }

    public PageRecuperarResponse recuperarPublicacionesByUserId(Long usuario_id, Pageable pageable) {
        return mapToResponsePageRecuperarPublicacion(
                publicacionRepository.findAllDisponiblesByUsuarioId(usuario_id, pageable)
                        .map(this::mapToResponseRecuperarPublicacion));
    }

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

    public Idioma recuperarIdioma(String idioma) {
        return idiomaRepository.findByNombre(idioma)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro el idioma con nombre: " + idioma));
    }
}
