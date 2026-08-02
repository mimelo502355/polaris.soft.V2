package com.epiis.app.business;

import com.epiis.app.dataaccess.PedidoItemRepository;
import com.epiis.app.dataaccess.PedidoRepository;
import com.epiis.app.dataaccess.ProductoRepository;
import com.epiis.app.dto.DtoPedidoItem;
import com.epiis.app.entity.Pedido;
import com.epiis.app.entity.PedidoItem;
import com.epiis.app.entity.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
public class PedidoItemBusiness {

    @Autowired
    private PedidoItemRepository pedidoItemRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    /**
     * ✅ Obtener items de un pedido
     */
    @Transactional(readOnly = true)
    public List<PedidoItem> getByPedidoId(String idPedido) {
        System.out.println("📦 getByPedidoId: " + idPedido);
        return pedidoItemRepository.findByPedidoIdPedido(idPedido);
    }

    /**
     * ✅ Obtener todos los items
     */
    @Transactional(readOnly = true)
    public List<PedidoItem> getAll() {
        return pedidoItemRepository.findAll();
    }

    /**
     * ✅ Insertar item
     */
    @Transactional
    public PedidoItem insert(String idPedido, DtoPedidoItem dto) {
        System.out.println("🛒 Insertando item en pedido: " + idPedido);
        System.out.println("   Acompañamientos: [" + dto.getAcompanamientos() + "]");

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + idPedido));

        Producto producto = productoRepository.findById(dto.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + dto.getIdProducto()));

        PedidoItem pedidoItem = new PedidoItem();
        pedidoItem.setIdItem(UUID.randomUUID().toString());
        pedidoItem.setProducto(producto);
        pedidoItem.setPedido(pedido);
        pedidoItem.setCantidad(dto.getCantidad());
        pedidoItem.setPrecioUnitarioFinal(new java.math.BigDecimal(dto.getPrecioUnitarioFinal()));
        pedidoItem.setAcompanamientos(dto.getAcompanamientos());

        Timestamp ahora = new Timestamp(System.currentTimeMillis());
        pedidoItem.setCreatedAt(ahora);
        pedidoItem.setUpdatedAt(ahora);

        return pedidoItemRepository.save(pedidoItem);
    }

    /**
     * ✅ Actualizar item
     */
    @Transactional
    public PedidoItem update(String idItem, DtoPedidoItem dto) {
        PedidoItem item = pedidoItemRepository.findById(idItem)
                .orElseThrow(() -> new RuntimeException("Item no encontrado: " + idItem));

        item.setCantidad(dto.getCantidad());
        item.setPrecioUnitarioFinal(new java.math.BigDecimal(dto.getPrecioUnitarioFinal()));
        item.setAcompanamientos(dto.getAcompanamientos());
        item.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        return pedidoItemRepository.save(item);
    }

    /**
     * ✅ Eliminar item
     */
    @Transactional
    public void delete(String idItem) {
        pedidoItemRepository.deleteById(idItem);
    }
}