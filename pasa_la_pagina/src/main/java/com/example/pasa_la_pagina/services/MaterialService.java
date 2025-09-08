package com.example.pasa_la_pagina.services;

import com.example.pasa_la_pagina.DTOs.requests.MaterialRequest;
import com.example.pasa_la_pagina.DTOs.response.MaterialResponse;
import com.example.pasa_la_pagina.entities.*;
import com.example.pasa_la_pagina.repositories.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final UsuarioRepository usuarioRepository;
    private final GradoRepository gradoRepository;
    private final MateriaRepository materiaRepository;
    private final CarreraRepository carreraRepository;
    private final InstitucionRepository institucionRepository;

    private MaterialResponse mapToResponse(Material material) {
        MaterialResponse response = new MaterialResponse();
        response.setId(material.getId());
        response.setTitulo(material.getTitulo());
        response.setDescripcion(material.getDescripcion());
        response.setNuevo(material.isNuevo());
        response.setDisponible(material.isDisponible());
        response.setDigital(material.isDigital());
        response.setLatitud(material.getLatitud());
        response.setLongitud(material.getLongitud());
        response.setIdioma(material.getIdioma());
        response.setPrecio(material.getPrecio());
        response.setTipoOferta(material.getTipoOferta());
        response.setUsuarioId(material.getUsuario() != null ? material.getUsuario().getId() : null);
        response.setGradoId(material.getGrado() != null ? material.getGrado().getId() : null);
        response.setMateriaId(material.getMateria() != null ? material.getMateria().getId() : null);
        response.setCarreraId(material.getCarrera() != null ? material.getCarrera().getId() : null);
        response.setInstitucionId(material.getInstitucion() != null ? material.getInstitucion().getId() : null);
        return response;
    }

    public List<MaterialResponse> getAll() {
        return materialRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<MaterialResponse> getByUsuarioId(Long usuarioId) {
        return materialRepository.findByUsuarioId(usuarioId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public MaterialResponse getById(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado con ID: " + id));
        return mapToResponse(material);
    }

    public MaterialResponse create(MaterialRequest request) {
        Material material = new Material();
        updateEntityFromRequest(material, request);
        return mapToResponse(materialRepository.save(material));
    }

    public MaterialResponse update(Long id, MaterialRequest request) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado con ID: " + id));
        updateEntityFromRequest(material, request);
        return mapToResponse(materialRepository.save(material));
    }

    public void delete(Long id) {
        if (!materialRepository.existsById(id)) {
            throw new RuntimeException("Material no encontrado con ID: " + id);
        }
        materialRepository.deleteById(id);
    }

    private void updateEntityFromRequest(Material material, MaterialRequest request) {
        material.setTitulo(request.getTitulo());
        material.setDescripcion(request.getDescripcion());
        material.setNuevo(request.isNuevo());
        material.setDisponible(request.isDisponible());
        material.setDigital(request.isDigital());
        material.setLatitud(request.getLatitud());
        material.setLongitud(request.getLongitud());
        material.setIdioma(request.getIdioma());
        material.setPrecio(request.getPrecio());
        material.setTipoOferta(request.getTipoOferta());

        if (request.getUsuarioId() != null) {
            material.setUsuario(usuarioRepository.findById(request.getUsuarioId()).orElse(null));
        }
        if (request.getGradoId() != null) {
            material.setGrado(gradoRepository.findById(request.getGradoId()).orElse(null));
        }
        if (request.getMateriaId() != null) {
            material.setMateria(materiaRepository.findById(request.getMateriaId()).orElse(null));
        }
        if (request.getCarreraId() != null) {
            material.setCarrera(carreraRepository.findById(request.getCarreraId()).orElse(null));
        }
        if (request.getInstitucionId() != null) {
            material.setInstitucion(institucionRepository.findById(request.getInstitucionId()).orElse(null));
        }
    }

}
