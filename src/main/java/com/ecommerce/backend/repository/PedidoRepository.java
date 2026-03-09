package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.EstadoPedido;
import com.ecommerce.backend.model.Pedido;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findAllByOrderByFechaDesc(); 

    @Query("SELECT p FROM Pedido p WHERE " +
           "(LOWER(p.nombreCliente) LIKE LOWER(CONCAT('%', :search, '%')) OR CAST(p.id AS string) LIKE CONCAT('%', :search, '%')) " +
           "AND p.estado = :estado")
    Page<Pedido> buscarPorTextoYEstadoPaginado(@Param("search") String search, @Param("estado") EstadoPedido estado, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE " +
           "LOWER(p.nombreCliente) LIKE LOWER(CONCAT('%', :search, '%')) OR CAST(p.id AS string) LIKE CONCAT('%', :search, '%')")
    Page<Pedido> buscarPorTextoPaginado(@Param("search") String search, Pageable pageable);

    Page<Pedido> findByEstado(EstadoPedido estado, Pageable pageable);
}