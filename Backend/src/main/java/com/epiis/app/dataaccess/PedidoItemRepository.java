package com.epiis.app.dataaccess;

import com.epiis.app.entity.PedidoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoItemRepository extends JpaRepository<PedidoItem, String> {

    /**
     * ✅ Obtener items por pedido ID
     * Usa JOIN FETCH para cargar Pedido y Producto eagerly
     */
    @Query("SELECT DISTINCT pi FROM PedidoItem pi " +
           "JOIN FETCH pi.pedido " +
           "JOIN FETCH pi.producto " +
           "WHERE pi.pedido.idPedido = :idPedido " +
           "ORDER BY pi.createdAt ASC")
    List<PedidoItem> findByPedidoIdPedido(@Param("idPedido") String idPedido);
}