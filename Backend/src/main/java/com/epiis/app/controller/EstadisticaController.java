package com.epiis.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.epiis.app.business.EstadisticaBusiness;
import com.epiis.app.dto.ProductoEstrellaDTO;
import com.epiis.app.dto.VentaSemanaDTO;

@RestController
@RequestMapping("/estadisticas") // 🔥 PLURAL, como Angular
@CrossOrigin(origins = "http://localhost:4200")
public class EstadisticaController {

    @Autowired
    private EstadisticaBusiness service;

    // 💰 Total hoy
    @GetMapping("/total-hoy")
    public Double totalHoy() {
        return service.totalHoy();
    }

    // 📆 Ventas semana
    @GetMapping("/ventas-semana")
    public List<VentaSemanaDTO> ventasSemana() {
        return service.ventasSemana();
    }

    // ⭐ Producto estrella
    @GetMapping("/producto-estrella")
    public List<ProductoEstrellaDTO> productoEstrella(
            @RequestParam String inicio,
            @RequestParam String fin) {
        return service.productoEstrella(inicio, fin);
    }
}
