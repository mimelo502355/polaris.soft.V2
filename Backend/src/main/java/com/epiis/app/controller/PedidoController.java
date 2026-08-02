package com.epiis.app.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.epiis.app.business.PedidoBusiness;
import com.epiis.app.dto.DtoPedido;
import com.epiis.app.dto.DtoPedidoDetalle;
import com.epiis.app.entity.Pedido;

@RestController
@RequestMapping("/pedido")
public class PedidoController {

    private final PedidoBusiness pedidoBusiness;

    public PedidoController(PedidoBusiness pedidoBusiness) {
        this.pedidoBusiness = pedidoBusiness;
    }

    @PostMapping(value = "/insert", consumes = "application/json")
    public ResponseEntity<?> insert(@RequestBody DtoPedido dto) {
        try {
            DtoPedido result = pedidoBusiness.insert(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("idPedido", result.getIdPedido()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensaje", "No se pudo registrar el pedido", "error", ex.getMessage()));
        }
    }

    @GetMapping("/getall")
    public List<DtoPedido> getAll() {
        return pedidoBusiness.getAll();
    }

    // ✅ NUEVO ENDPOINT: Obtener pedidos de un cliente
    @GetMapping("/cliente/{nombreCliente}")
    public ResponseEntity<?> getPedidosByCliente(@PathVariable String nombreCliente) {
        try {
            System.out.println("📥 GET /pedido/cliente/" + nombreCliente);
            List<DtoPedido> pedidos = pedidoBusiness.getPedidosByCliente(nombreCliente);
            System.out.println("✅ Retornando " + pedidos.size() + " pedidos para " + nombreCliente);
            return ResponseEntity.ok(pedidos);
        } catch (Exception ex) {
            System.err.println("❌ ERROR: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    // 🟢 Conserva la devolución de 'estado' y 'motivoCancelacion' para compatibilidad con el frontend
    @GetMapping("/estado/{idPedido}")
    public ResponseEntity<?> getEstado(@PathVariable String idPedido) {
        try {
            Pedido pedido = pedidoBusiness.obtenerPedidoPorId(idPedido);
            return ResponseEntity.ok(Map.of(
                "estado", pedido.getEstado().name(),
                "motivoCancelacion", pedido.getMotivoCancelacion() != null ? pedido.getMotivoCancelacion() : ""
            ));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        }
    }

    @PutMapping("/estado/{idPedido}")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable String idPedido,
            @RequestBody Map<String, String> body) {
        pedidoBusiness.cambiarEstado(idPedido, body.get("estado"));
        return ResponseEntity.ok(Map.of("mensaje", "Estado actualizado"));
    }

    @GetMapping("/detalle/{idPedido}")
    public ResponseEntity<DtoPedidoDetalle> getDetallePedido(@PathVariable String idPedido) {
        DtoPedidoDetalle detalle = pedidoBusiness.getPedidoConDetalle(idPedido);
        return ResponseEntity.ok(detalle);
    }

    // 1. Cliente solicita cancelación
    @PutMapping("/{id}/solicitar-cancelacion")
    public ResponseEntity<?> solicitarCancelacion(@PathVariable String id, @RequestBody Map<String, String> payload) {
        try {
            String motivo = payload.getOrDefault("motivoCancelacion", payload.get("motivo"));
            pedidoBusiness.solicitarCancelacion(id, motivo);
            return ResponseEntity.ok(Map.of("mensaje", "Solicitud de cancelación enviada correctamente"));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    // 2. Empleado responde la solicitud (Acepta o Rechaza)
    @PutMapping("/{id}/responder-cancelacion")
    public ResponseEntity<?> responderCancelacion(@PathVariable String id, @RequestBody Map<String, Boolean> payload) {
        try {
            Boolean aceptar = payload.get("aceptar");
            boolean aprobado = Boolean.TRUE.equals(aceptar);
            pedidoBusiness.responderCancelacion(id, aprobado);
            return ResponseEntity.ok(Map.of("mensaje", "Respuesta procesada correctamente"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/finalizar")
    public ResponseEntity<?> finalizarPedido(@RequestBody Map<String, Object> body) {
        String idPedido = String.valueOf(body.get("idPedido"));
        pedidoBusiness.finalizarPedido(idPedido);
        return ResponseEntity.ok(Map.of("mensaje", "Pedido finalizado"));
    }
}