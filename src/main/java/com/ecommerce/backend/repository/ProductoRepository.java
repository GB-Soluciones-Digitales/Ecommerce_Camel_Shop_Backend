package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    Page<Producto> findByActivoTrue(Pageable pageable);
    
    Page<Producto> findByCategoriaNombreIgnoreCaseAndActivoTrue(String nombreCat, Pageable pageable);
    
    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND p.activo = true")
    Page<Producto> buscarPorNombrePaginado(@Param("nombre") String nombre, Pageable pageable);
    
    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) " +
           "AND LOWER(p.categoria.nombre) = LOWER(:categoriaNombre) AND p.activo = true")
    Page<Producto> buscarPorNombreYCategoriaPaginado(@Param("nombre") String nombre, 
                                                     @Param("categoriaNombre") String categoriaNombre, 
                                                     Pageable pageable);

    Optional<Producto> findBySlugAndActivoTrue(String slug);
}