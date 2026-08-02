package com.epiis.app.business;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.epiis.app.dataaccess.EstadisticaRepository;
import com.epiis.app.dto.VentaSemanaDTO;
import com.epiis.app.dto.ProductoEstrellaDTO;

@Service
public class EstadisticaBusiness {

    @Autowired
    private EstadisticaRepository repo;

    // Total vendido hoy
    public Double totalHoy() {
        return repo.totalVendidoHoy();
    }

    // Ventas por semana
    public List<VentaSemanaDTO> ventasSemana() {
        return repo.ventasSemana();
    }

    // Producto estrella
    public List<ProductoEstrellaDTO> productoEstrella(String inicio, String fin) {
        return repo.productoEstrella(inicio, fin);
    }
}
