package com.epiis.app.dataaccess;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.epiis.app.entity.Pedido;
import com.epiis.app.entity.Pedido.EstadoPedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, String> {
    
    // ✅ Obtener pedidos con sus items
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.items WHERE p.idPedido = :idPedido")
    Optional<Pedido> findByIdWithItems(@Param("idPedido") String idPedido);
    
    // ✅ Obtener todos los pedidos con sus detalles
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.items")
    List<Pedido> findAllWithDetalles();
    
    // ✅ Obtener estado por ID
    @Query("SELECT p.estado FROM Pedido p WHERE p.idPedido = :idPedido")
    Optional<EstadoPedido> obtenerEstadoPorId(@Param("idPedido") String idPedido);
    
    // ✅ NUEVO: Buscar pedidos por nombre de cliente (SIN filtro de estado)
    @Query("SELECT p FROM Pedido p WHERE p.nombreCliente = :nombreCliente ORDER BY p.createdAt DESC")
    List<Pedido> findByNombreCliente(@Param("nombreCliente") String nombreCliente);
    
    // ✅ NUEVO: Buscar pedidos por cliente con items (para evitar lazy loading)
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.items WHERE p.nombreCliente = :nombreCliente ORDER BY p.createdAt DESC")
    List<Pedido> findByNombreClienteWithItems(@Param("nombreCliente") String nombreCliente);
    
    // ✅ NUEVO: Buscar pedidos FINALIZADOS de un cliente
    @Query("SELECT p FROM Pedido p WHERE p.nombreCliente = :nombreCliente AND p.estado = 'FINALIZADO' ORDER BY p.createdAt DESC")
    List<Pedido> findByNombreClienteAndEstadoFinalizado(@Param("nombreCliente") String nombreCliente);
    
    // ✅ Obtener pedidos por estado (para panel empleado)
    @Query("SELECT p FROM Pedido p WHERE p.estado = :estado ORDER BY p.createdAt DESC")
    List<Pedido> findByEstado(@Param("estado") EstadoPedido estado);
}