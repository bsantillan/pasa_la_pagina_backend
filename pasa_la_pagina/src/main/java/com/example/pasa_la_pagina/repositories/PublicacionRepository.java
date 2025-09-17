package com.example.pasa_la_pagina.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.pasa_la_pagina.entities.Publicacion;
import com.example.pasa_la_pagina.entities.Enum.NivelEducativo;
import com.example.pasa_la_pagina.entities.Enum.TipoOferta;

public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {

        @Query("SELECT DISTINCT p FROM Publicacion p " +
                        "JOIN FETCH p.material m " +
                        "LEFT JOIN FETCH m.fotos f " +
                        "LEFT JOIN Libro l ON m.id = l.id " +
                        "LEFT JOIN l.genero g " +
                        "LEFT JOIN l.autor a " +
                        "LEFT JOIN l.editorial e " +
                        "WHERE TYPE(m) = Libro " + // <-- filtra solo libros
                        "AND (:query IS NULL OR " +
                        "   LOWER(m.titulo) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "   OR LOWER(m.descripcion) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "   OR LOWER(l.isbn) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "   OR LOWER(g.nombre) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "   OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "   OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :query, '%'))" +
                        ") " +
                        "AND (:nuevo IS NULL OR m.nuevo = :nuevo) " +
                        "AND (:digital IS NULL OR m.digital = :digital) " +
                        "AND (:idiomas IS NULL OR m.idioma IN :idiomas) " +
                        "AND (:tiposOfertas IS NULL OR p.tipo_oferta IN :tiposOfertas) " +
                        "AND (:precioMinimo IS NULL OR p.precio >= :precioMinimo) " +
                        "AND (:precioMaximo IS NULL OR p.precio <= :precioMaximo)" +
                        "AND p.disponible = true")
        List<Publicacion> buscarPorLibroDisponibles(
                        @Param("query") String query,
                        @Param("nuevo") Boolean nuevo,
                        @Param("digital") Boolean digital,
                        @Param("idiomas") List<String> idiomas,
                        @Param("tiposOfertas") List<TipoOferta> tiposOfertas,
                        @Param("precioMinimo") Double precioMinimo,
                        @Param("precioMaximo") Double precioMaximo);

        @Query("SELECT DISTINCT p FROM Publicacion p " +
                        "JOIN FETCH p.material m " +
                        "LEFT JOIN FETCH m.fotos f " +
                        "LEFT JOIN Apunte a ON m.id = a.id " +
                        "LEFT JOIN a.materia mat " +
                        "LEFT JOIN a.institucion inst " +
                        "LEFT JOIN a.seccion sec " +
                        "LEFT JOIN a.carrera car " +
                        "WHERE TYPE(m) = Apunte " + // <-- filtra solo apuntes
                        "AND (:query IS NULL OR " +
                        "   LOWER(m.titulo) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "   OR LOWER(m.descripcion) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "   OR LOWER(mat.nombre) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "   OR LOWER(inst.nombre) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "   OR LOWER(sec.nombre) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "   OR LOWER(car.nombre) LIKE LOWER(CONCAT('%', :query, '%'))" +
                        ") " +
                        "AND (:nivelesEducativos IS NULL OR inst.nivelEducativo IN :nivelesEducativos) " +
                        "AND (:nuevo IS NULL OR m.nuevo = :nuevo) " +
                        "AND (:digital IS NULL OR m.digital = :digital) " +
                        "AND (:idiomas IS NULL OR m.idioma IN :idiomas) " +
                        "AND (:tiposOfertas IS NULL OR p.tipo_oferta IN :tiposOfertas) " +
                        "AND (:precioMinimo IS NULL OR p.precio >= :precioMinimo) " +
                        "AND (:precioMaximo IS NULL OR p.precio <= :precioMaximo)" +
                        "AND p.disponible = true")
        List<Publicacion> buscarPorApunteDisponibles(
                        @Param("query") String query,
                        @Param("nuevo") Boolean nuevo,
                        @Param("digital") Boolean digital,
                        @Param("idiomas") List<String> idiomas,
                        @Param("tiposOfertas") List<TipoOferta> tiposOfertas,
                        @Param("nivelesEducativos") List<NivelEducativo> nivelesEducativos,
                        @Param("precioMinimo") Double precioMinimo,
                        @Param("precioMaximo") Double precioMaximo);

        @Query("SELECT p FROM Publicacion p " +
                        "WHERE p.disponible = true ")
        Page<Publicacion> findAllDisponiblesByUsuarioId(Long usuarioId, Pageable pageable);

        @Query("SELECT p FROM Publicacion p " +
                        "WHERE p.disponible = true ")
        Page<Publicacion> findAllDisponibles(Pageable pageable);

        @Query("SELECT p FROM Publicacion p " +
                        "WHERE p.disponible = true "+
                        "AND p.id = :id")
        Optional<Publicacion> findDisponibleById(Long id);

        @Query("SELECT p FROM Publicacion p " +
                        "WHERE p.disponible = true " +
                        "ORDER BY (6371 * acos( " +
                        "cos(radians(:usuarioLatitud)) * cos(radians(p.latitud)) * " +
                        "cos(radians(p.longitud) - radians(:usuarioLongitud)) + " +
                        "sin(radians(:usuarioLatitud)) * sin(radians(p.latitud))" +
                        ")) ASC")
        Page<Publicacion> findAllDisponiblesOrderByDistance(
                        @Param("usuarioLatitud") Double usuarioLatitud,
                        @Param("usuarioLongitud") Double usuarioLongitud,
                        Pageable pageable);

}
