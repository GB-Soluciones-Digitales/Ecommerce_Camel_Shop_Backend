package com.ecommerce.backend.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.backend.dto.PedidoCreateDTO;
import com.ecommerce.backend.model.EstadoPedido;
import com.ecommerce.backend.model.Pedido;
import com.ecommerce.backend.service.FileStorageService;
import com.ecommerce.backend.service.PedidoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;
    private final FileStorageService fileService;

    // Crear pedido
    @PostMapping("/publico")
    public ResponseEntity<Pedido> crearPedido(@RequestBody PedidoCreateDTO dto) {
        return ResponseEntity.ok(pedidoService.crearPedido(dto));
    }

    // Crear pedido manual
    @PostMapping("/admin/manual")
    public ResponseEntity<Pedido> crearPedidoManual(@RequestBody PedidoCreateDTO dto) {
        return ResponseEntity.ok(pedidoService.crearPedido(dto));
    }

    // Listar pedidos
    @GetMapping("/admin")
    public ResponseEntity<Page<Pedido>> listarPedidos(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) EstadoPedido estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(pedidoService.obtenerPedidosPaginados(search, estado, pageable));
    }

    // Cambiar estado
    @PatchMapping("/admin/{id}/estado")
    public ResponseEntity<Pedido> cambiarEstado(@PathVariable Long id, @RequestParam EstadoPedido estado) {
        return ResponseEntity.ok(pedidoService.cambiarEstado(id, estado));
    }

    @PostMapping("/admin/{id}/factura")
    public ResponseEntity<Pedido> subirFactura(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        String nombreArchivo = fileService.storeFile(file);
        
        Pedido pedido = pedidoService.getPedidoById(id); 
        pedido.setFacturaUrl(nombreArchivo);

        Pedido actualizado = pedidoService.actualizarPedido(pedido); 
        
        return ResponseEntity.ok(actualizado);
    }
}