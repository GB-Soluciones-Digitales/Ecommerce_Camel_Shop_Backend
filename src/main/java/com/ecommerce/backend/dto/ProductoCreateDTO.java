package com.ecommerce.backend.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoCreateDTO {
    @NotBlank @Size(min = 3, max = 100)
    private String nombre;
    
    @Size(max = 500)
    private String descripcion;
    
    @NotNull @DecimalMin("0.0")
    private BigDecimal precio;

    private Boolean enOferta;
    
    private Integer porcentajeDescuento;
    
    private List<String> imagenes;
    
    @NotNull
    private Long categoriaId;

    private List<VarianteDTO> variantes;

    @Data
    public static class VarianteDTO {
        private String color;
        private Map<String, Integer> stockPorTalle;
    }
}