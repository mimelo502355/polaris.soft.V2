package com.epiis.app.dataaccess;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.epiis.app.dto.ProductoEstrellaDTO;
import com.epiis.app.dto.VentaSemanaDTO;
import com.epiis.app.entity.Pedido;

public interface EstadisticaRepository extends JpaRepository<Pedido, String> {

    // 💰 Total vendido HOY (PostgreSQL)
    @Query(value = """
        SELECT SUM(p."totalPagar")
        FROM "pedido" p
        WHERE DATE(p."createdAt") = CURRENT_DATE
    """, nativeQuery = true)
    Double totalVendidoHoy();

    // 📆 Ventas últimos 7 días (PostgreSQL)
    @Query(value = """
        SELECT 
          DATE(p."createdAt") AS dia,
          SUM(p."totalPagar") AS total
        FROM "pedido" p
        WHERE p."createdAt" >= CURRENT_DATE - INTERVAL '6 days'
        GROUP BY DATE(p."createdAt")
        ORDER BY DATE(p."createdAt")
    """, nativeQuery = true)
    List<VentaSemanaDTO> ventasSemana();

    // ⭐ Producto estrella por rango de fechas (PostgreSQL)
    @Query(value = """
        SELECT 
          pr."nombre" AS nombre,
          SUM(CAST(pi."cantidad" AS INTEGER)) AS totalVendido
        FROM "pedidoitem" pi
        JOIN "producto" pr ON pr."idProducto" = pi."idProducto"
        JOIN "pedido" p ON p."idPedido" = pi."idPedido"
        WHERE p."createdAt" BETWEEN 
              TO_TIMESTAMP(:inicio, 'YYYY-MM-DD')
          AND TO_TIMESTAMP(:fin, 'YYYY-MM-DD') + INTERVAL '1 day'
        GROUP BY pr."nombre"
        ORDER BY totalVendido DESC
    """, nativeQuery = true)
    List<ProductoEstrellaDTO> productoEstrella(
        @Param("inicio") String inicio,
        @Param("fin") String fin
    );
}