package com.example.pasa_la_pagina.DTOs.requests;

import java.util.List;

import com.example.pasa_la_pagina.entities.Enum.TipoOferta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PublicacionLibroRequest {
    @NotBlank(message = "El titulo es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripccion es obligatoria")
    private String descripcion;

    @NotNull(message = "El si es nuevo es obligatorio")
    private Boolean nuevo;

    @NotNull(message = "El si es digital es obligatorio")
    private Boolean digital;

    @NotNull(message = "La latitud es obligatoria")
    private Double latitud;

    @NotNull(message = "La longitud es obligatoria")
    private Double longitud;

    @NotBlank(message = "El idioma es obligatorio")
    private String idioma;

    @Positive(message = "El precio tiene que ser mayor que 0")
    private Double precio;

    @NotNull(message = "La cantidad de material es obligatorio")
    @Positive(message = "La cantidad tiene que ser mayor que 0")
    private Integer cantidad;

    @NotNull(message = "El tipo de oferta es obligatorio")
    private TipoOferta tipo_oferta;

    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;

    @NotEmpty(message = "Las URLs de las fotos son obligatorias")
    private List<String> fotos_url;

    @NotBlank(message = "El ISBN es obligatorio")
    private String isbn;

    @NotBlank(message = "El genero es obligatorio")
    private String genero;

    @NotBlank(message = "El autor es obligatorio")
    private String autor;

    @NotBlank(message = "La editorial es obligatoria")
    private String editorial;
}
