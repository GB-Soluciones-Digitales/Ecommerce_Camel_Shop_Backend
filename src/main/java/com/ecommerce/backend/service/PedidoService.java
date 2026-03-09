package com.ecommerce.backend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.backend.dto.PedidoCreateDTO;
import com.ecommerce.backend.model.DetallePedido;
import com.ecommerce.backend.model.EstadoPedido;
import com.ecommerce.backend.model.Pedido;
import com.ecommerce.backend.model.Producto;
import com.ecommerce.backend.model.ProductoVariantes;
import com.ecommerce.backend.repository.PedidoRepository;
import com.ecommerce.backend.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public Pedido crearPedido(PedidoCreateDTO dto) {
        Pedido pedido = new Pedido();
        pedido.setNombreCliente(dto.getNombreCliente());
        pedido.setTelefono(dto.getTelefono());
        pedido.setDireccionEnvio(dto.getDireccionEnvio());
        pedido.setMetodoPago(dto.getMetodoPago());
        pedido.setEstado(EstadoPedido.PENDIENTE);

        List<DetallePedido> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (PedidoCreateDTO.ItemPedidoDTO itemDto : dto.getItems()) {
            Producto producto = productoRepository.findById(itemDto.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + itemDto.getProductoId()));

            procesarDescuentoStock(producto, itemDto.getTalle(), itemDto.getCantidad());

            productoRepository.save(producto);

            BigDecimal precio = producto.getPrecio();
            BigDecimal cantidad = new BigDecimal(itemDto.getCantidad());
            BigDecimal subtotal = precio.multiply(cantidad);

            DetallePedido detalle = DetallePedido.builder()
                    .producto(producto)
                    .cantidad(itemDto.getCantidad())
                    .talleSeleccionado(itemDto.getTalle())
                    .precioUnitario(precio)
                    .subtotal(subtotal)
                    .build();
            
            detalles.add(detalle);
            total = total.add(subtotal);
        }

        pedido.setDetalles(detalles);
        pedido.setTotal(total);

        return pedidoRepository.save(pedido);
    }

    private void procesarDescuentoStock(Producto producto, String talleString, Integer cantidad) {
        if (producto.getStock() < cantidad) {
            throw new RuntimeException("No hay stock global suficiente para: " + producto.getNombre());
        }

        producto.setStock(producto.getStock() - cantidad);

        if (talleString != null && talleString.contains("-")) {
            String[] partes = talleString.split("-");
            if (partes.length >= 2) {
                String color = partes[0].trim();
                String talle = partes[1].trim();

                ProductoVariantes variante = producto.getVariantes().stream()
                    .filter(v -> v.getColor().equalsIgnoreCase(color))
                    .findFirst()
                    .orElse(null);

                if (variante != null) {
                    Map<String, Integer> stockMap = variante.getStockPorTalle();
                    Integer stockActual = stockMap.getOrDefault(talle, 0);

                    if (stockActual < cantidad) {
                        throw new RuntimeException("No hay stock suficiente para " + color + " talle " + talle);
                    }

                    stockMap.put(talle, stockActual - cantidad);
                }
            }
        }
    }

    public Page<Pedido> obtenerPedidosPaginados(String search, EstadoPedido estado, Pageable pageable) {
        
        boolean tieneBusqueda = (search != null && !search.trim().isEmpty());
        boolean tieneEstado = (estado != null);

        if (tieneBusqueda && tieneEstado) {
            return pedidoRepository.buscarPorTextoYEstadoPaginado(search, estado, pageable);
            
        } else if (tieneBusqueda) {
            return pedidoRepository.buscarPorTextoPaginado(search, pageable);
            
        } else if (tieneEstado) {
            return pedidoRepository.findByEstado(estado, pageable);
            
        } else {
            return pedidoRepository.findAll(pageable);
        }
    }
    
    @Transactional
    public Pedido cambiarEstado(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        pedido.setEstado(nuevoEstado);
        
        if (nuevoEstado == EstadoPedido.CANCELADO) {
            devolverStock(pedido);
        }
        
        return pedidoRepository.save(pedido);
    }

    private void devolverStock(Pedido pedido) {
        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto p = detalle.getProducto();
            p.setStock(p.getStock() + detalle.getCantidad());

            String talleString = detalle.getTalleSeleccionado();
            if (talleString != null && talleString.contains("-")) {
                String[] partes = talleString.split("-");
                String color = partes[0].trim();
                String talle = partes[1].trim();

                p.getVariantes().stream()
                    .filter(v -> v.getColor().equalsIgnoreCase(color))
                    .findFirst()
                    .ifPresent(v -> {
                        int current = v.getStockPorTalle().getOrDefault(talle, 0);
                        v.getStockPorTalle().put(talle, current + detalle.getCantidad());
                    });
            }
            productoRepository.save(p);
        }
    }

    public Pedido getPedidoById(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
    }

    public Pedido actualizarPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }
}