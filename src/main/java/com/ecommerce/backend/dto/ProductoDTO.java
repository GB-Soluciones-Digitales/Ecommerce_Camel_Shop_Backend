package com.ecommerce.backend.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String slug;
    private BigDecimal precio;
    private Integer stock;
    private List<String> imagenes;
    private Boolean activo;
    private Long categoriaId;
    private String categoriaNombre;
    private List<VarianteDTO> variantes;

    @Data
    @Builder
    public static class VarianteDTO {
        private String color;
        private Map<String, Integer> stockPorTalle;
    }
}