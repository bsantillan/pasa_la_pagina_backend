package com.example.pasa_la_pagina.services;

import org.springframework.stereotype.Service;

import com.example.pasa_la_pagina.DTOs.requests.PublicacionLibroRequest;
import com.example.pasa_la_pagina.DTOs.response.PublicacionLibroResponse;
import com.example.pasa_la_pagina.entities.Autor;
import com.example.pasa_la_pagina.entities.Editorial;
import com.example.pasa_la_pagina.entities.Foto;
import com.example.pasa_la_pagina.entities.Genero;
import com.example.pasa_la_pagina.entities.Libro;
import com.example.pasa_la_pagina.repositories.AutorRepository;
import com.example.pasa_la_pagina.repositories.EditorialRepository;
import com.example.pasa_la_pagina.repositories.GeneroRepository;
import com.example.pasa_la_pagina.repositories.LibroRepository;
import com.example.pasa_la_pagina.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LibroService {
    
    private final LibroRepository libroRepository;
    private final UsuarioRepository usuarioRepository;
    private final EditorialRepository editorialRepository;
    private final GeneroRepository generoRepository;
    private final AutorRepository autorRepository;

    private PublicacionLibroResponse mapToResponse(Libro libro) {
        PublicacionLibroResponse response = new PublicacionLibroResponse();
        response.setId(libro.getId());
        response.setTitulo(libro.getTitulo());
        response.setDescripcion(libro.getDescripcion());
        response.setNuevo(libro.isNuevo());
        response.setIdioma(libro.getIdioma());
        response.setIsbn(libro.getIsbn());
        response.setEditorial(libro.getEditorial().getNombre());
        response.setAutor(libro.getAutor().getNombre());
        response.setGenero(libro.getGenero().getNombre());
        response.setFotos(libro.getFotos().stream().map(Foto::getUrl).toList());
        return response;
    }

    public PublicacionLibroResponse nuevoLibro(PublicacionLibroRequest request){
        Editorial editorial = recuperarEditorial(request.getEditorial());
        Genero genero = recuperarGenero(request.getGenero());
        Autor autor = recuperarAutor(request.getAutor());

        Libro libro = Libro.builder()
                        .titulo(request.getTitulo())
                        .descripcion(request.getDescripcion())
                        .nuevo(request.isNuevo())
                        .idioma(request.getIdioma())
                        .editorial(editorial)
                        .genero(genero)
                        .autor(autor)
                        .build();
        
        libroRepository.save(libro);
        return mapToResponse(libro);
    }

    public Editorial recuperarEditorial(String editorial){
        return editorialRepository.findByNombre(editorial).orElse(editorialRepository.save(Editorial.builder().nombre(editorial).build()));
    }

    public Genero recuperarGenero(String genero){
        return generoRepository.findByNombre(genero).orElse(generoRepository.save(Genero.builder().nombre(genero).build()));
    }

    public Autor recuperarAutor(String autor){
        return autorRepository.findByNombre(autor).orElse(autorRepository.save(Autor.builder().nombre(autor).build()));
    }
}
