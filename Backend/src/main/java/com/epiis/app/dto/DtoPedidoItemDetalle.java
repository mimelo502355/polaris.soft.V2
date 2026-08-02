package com.epiis.app.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoPedidoItemDetalle {
    private String idItem;
    private String nombreProducto;
    private Integer cantidad;
    private BigDecimal precioUnitarioFinal;
    
    // ✅ NUEVOS CAMPOS PARA ACOMPAÑAMIENTOS
    private List<String> toppings;
    private List<String> salsas;
    private List<String> toppingsIds;
    private List<String> salsasIds;
    
    // ✅ GETTER PARA MOSTRAR ACOMPAÑAMIENTOS EN FORMATO LEGIBLE
    public String getAcompañamientosFormato() {
        StringBuilder sb = new StringBuilder();
        
        if (toppings != null && !toppings.isEmpty()) {
            sb.append("🧀 Toppings: ").append(String.join(", ", toppings));
        }
        
        if (salsas != null && !salsas.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" | ");
            }
            sb.append("🌶️ Salsas: ").append(String.join(", ", salsas));
        }
        
        return sb.length() > 0 ? sb.toString() : "Sin acompañamientos";
    }
    
    // ✅ GETTER PARA CONTAR ACOMPAÑAMIENTOS
    public int contarAcompañamientos() {
        int count = 0;
        if (toppings != null) count += toppings.size();
        if (salsas != null) count += salsas.size();
        return count;
    }
}