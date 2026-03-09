package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.ProductoCreateDTO;
import com.ecommerce.backend.dto.ProductoDTO;
import com.ecommerce.backend.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/productos")
@CrossOrigin(origins = "*")
public class ProductoController {
    
    @Autowired
    private ProductoService productoService;
    
    // ===== ENDPOINTS PÚBLICOS =====
    
    @GetMapping("/publico")
    public ResponseEntity<Page<ProductoDTO>> obtenerProductos(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String categoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(productoService.obtenerProductosPublicos(search, categoria, pageable));
    }

    @GetMapping("/publico/{id}")
    public ResponseEntity<ProductoDTO> obtenerProductoPublico(@PathVariable Long id) {
        ProductoDTO dto = productoService.obtenerProductoPorId(id);
        return dto.getActivo() ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @GetMapping("/publico/detalle/{slug}")
    public ResponseEntity<ProductoDTO> obtenerProductoPublicoPorSlug(@PathVariable String slug) {
        try {
            return ResponseEntity.ok(productoService.obtenerProductoPorSlug(slug));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ===== ENDPOINTS DE ADMINISTRACIÓN =====
    
    @GetMapping("/admin")
    public ResponseEntity<Page<ProductoDTO>> obtenerTodosProductos(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // Ordenamos por ID descendente para que los últimos creados salgan primero
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(productoService.obtenerProductosAdminPaginados(search, pageable));
    }
    
    @GetMapping("/admin/{id}")
    public ResponseEntity<ProductoDTO> obtenerProductoAdmin(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productoService.obtenerProductoPorId(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/admin")
    public ResponseEntity<ProductoDTO> crearProducto(@Valid @RequestBody ProductoCreateDTO dto) {
        try {
            ProductoDTO creado = productoService.crearProducto(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/admin/{id}")
    public ResponseEntity<ProductoDTO> actualizarProducto(
            @PathVariable Long id, 
            @Valid @RequestBody ProductoCreateDTO dto) {
        try {
            ProductoDTO actualizado = productoService.actualizarProducto(id, dto);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PatchMapping("/admin/{id}/toggle")
    public ResponseEntity<ProductoDTO> toggleEstadoProducto(@PathVariable Long id) {
        try {
            ProductoDTO actualizado = productoService.toggleEstadoProducto(id);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        try {
            productoService.eliminarProducto(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
