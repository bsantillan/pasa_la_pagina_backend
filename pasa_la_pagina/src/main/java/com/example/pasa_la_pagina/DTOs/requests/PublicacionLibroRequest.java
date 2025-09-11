package com.example.pasa_la_pagina.DTOs.requests;

import java.util.List;

import com.example.pasa_la_pagina.entities.Enum.TipoOferta;

import jakarta.validation.constraints.NotBlank;
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
    private boolean nuevo;

    @NotNull(message = "El si esta disponible es obligatorio")
    private boolean disponible;

    @NotNull(message = "El si es digital es obligatorio")
    private boolean digital;

    @NotNull(message = "La latitud es obligatoria")
    private Double latitud;

    @NotNull(message = "La longitud es obligatoria")
    private Double longitud;

    @NotBlank(message = "El idioma es obligatorio")
    private String idioma;

    @Positive(message = "El precio tiene que ser mayor que 0")
    private Double precio;

    @Positive(message = "La cantidad tiene que ser mayor que 0")
    private Integer cantidad;

    @NotNull(message = "El tipo de oferta es obligatorio")
    private TipoOferta tipoOferta;

    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "Las URLs de las fotos son obligatorias")
    private List<String> fotos_url;

    @NotBlank(message = "El genero es obligatorio")
    private String genero;

    @NotBlank(message = "El autor es obligatorio")
    private String autor;

    @NotBlank(message = "La editorial es obligatoria")
    private String editorial;
}
