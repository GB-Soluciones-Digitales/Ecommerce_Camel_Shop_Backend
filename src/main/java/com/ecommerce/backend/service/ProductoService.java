package com.ecommerce.backend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.backend.dto.ProductoCreateDTO;
import com.ecommerce.backend.dto.ProductoDTO;
import com.ecommerce.backend.model.Categoria;
import com.ecommerce.backend.model.Producto;
import com.ecommerce.backend.model.ProductoVariantes;
import com.ecommerce.backend.repository.CategoriaRepository;
import com.ecommerce.backend.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {
    
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    // Lógica para el catálogo público
    public Page<ProductoDTO> obtenerProductosPublicos(String search, String categoria, Pageable pageable) {
        Page<Producto> productos;

        if (search != null && !search.isEmpty() && categoria != null && !categoria.isEmpty()) {
            productos = productoRepository.buscarPorNombreYCategoriaPaginado(search, categoria, pageable);
        } else if (search != null && !search.isEmpty()) {
            productos = productoRepository.buscarPorNombrePaginado(search, pageable);
        } else if (categoria != null && !categoria.isEmpty()) {
            productos = productoRepository.findByCategoriaNombreIgnoreCaseAndActivoTrue(categoria, pageable);
        } else {
            productos = productoRepository.findByActivoTrue(pageable);
        }

        return productos.map(this::convertirADTO);
    }

    // Endpoints de Administración
    public Page<ProductoDTO> obtenerProductosAdminPaginados(String search, Pageable pageable) {
        Page<Producto> productos;

        if (search != null && !search.isEmpty()) {
            productos = productoRepository.buscarPorNombrePaginado(search, pageable);
        } else {
            productos = productoRepository.findAll(pageable);
        }

        return productos.map(this::convertirADTO);
    }

    public ProductoDTO obtenerProductoPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return convertirADTO(producto);
    }

    @Transactional
    public ProductoDTO crearProducto(ProductoCreateDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        
        Producto producto = Producto.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .precio(dto.getPrecio())
                .imagenes(dto.getImagenes())
                .categoria(categoria)
                .activo(true)
                .build();
        
        procesarVariantes(producto, dto.getVariantes());
        return convertirADTO(productoRepository.save(producto));
    }

    @Transactional
    public ProductoDTO actualizarProducto(Long id, ProductoCreateDTO dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setImagenes(dto.getImagenes());
        
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        producto.setCategoria(categoria);

        producto.getVariantes().clear();
        procesarVariantes(producto, dto.getVariantes());
        
        return convertirADTO(productoRepository.save(producto));
    }

    @Transactional
    public ProductoDTO toggleEstadoProducto(Long id) {
        Producto producto = productoRepository.findById(id).orElseThrow();
        producto.setActivo(!producto.getActivo());
        return convertirADTO(productoRepository.save(producto));
    }

    @Transactional
    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    // Métodos Auxiliares
    private void procesarVariantes(Producto producto, List<ProductoCreateDTO.VarianteDTO> variantesDto) {
        if (variantesDto == null) return;
        int stockTotal = 0;
        for (ProductoCreateDTO.VarianteDTO vDto : variantesDto) {
            Map<String, Integer> stockMapa = vDto.getStockPorTalle() != null ? vDto.getStockPorTalle() : new HashMap<>();
            ProductoVariantes variante = ProductoVariantes.builder()
                    .color(vDto.getColor() != null ? vDto.getColor() : "Único")
                    .stockPorTalle(stockMapa)
                    .build();
            producto.addVariante(variante);
            stockTotal += stockMapa.values().stream().filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
        }
        producto.setStock(stockTotal);
    }

    private ProductoDTO convertirADTO(Producto producto) {
        return ProductoDTO.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .imagenes(producto.getImagenes())
                .activo(producto.getActivo())
                .categoriaId(producto.getCategoria().getId())
                .categoriaNombre(producto.getCategoria().getNombre())
                .variantes(producto.getVariantes().stream().map(v -> 
                    ProductoDTO.VarianteDTO.builder().color(v.getColor()).stockPorTalle(v.getStockPorTalle()).build()
                ).collect(Collectors.toList()))
                .build();
    }
}