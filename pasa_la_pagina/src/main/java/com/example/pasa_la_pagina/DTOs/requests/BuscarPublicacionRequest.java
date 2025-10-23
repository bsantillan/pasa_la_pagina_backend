package com.example.pasa_la_pagina.DTOs.requests;

import java.util.List;

import com.example.pasa_la_pagina.entities.Enum.NivelEducativo;
import com.example.pasa_la_pagina.entities.Enum.TipoMaterial;
import com.example.pasa_la_pagina.entities.Enum.TipoOferta;

import lombok.Data;

@Data
public class BuscarPublicacionRequest {
    private String query; // Se puede escribir el titulo, descripcion, materia, institucion, seccion, carrera, isbn, editorial, genero, autor
    
    private Boolean nuevo;
    private Boolean digital;
    private List<String> idiomas;
    private List<TipoOferta> tipos_oferta;
    private Double precio_minimo;
    private Double precio_maximo;

    private List<TipoMaterial> tipos_material;
    private List<NivelEducativo> niveles_educativos;

}
