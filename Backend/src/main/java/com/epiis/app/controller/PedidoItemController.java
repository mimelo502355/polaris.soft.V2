package com.epiis.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.epiis.app.business.PedidoItemBusiness;
import com.epiis.app.dto.DtoPedidoItem;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pedidoItem")
@CrossOrigin(origins = "http://localhost:4200")
public class PedidoItemController {

    @Autowired
    private PedidoItemBusiness pedidoItemBusiness;

    /**
     * ✅ OBTENER ITEMS DE UN PEDIDO
     * GET: /pedidoItem/pedido/{idPedido}
     * DEVUELVE DTOs con acompañamientos
     */
    @GetMapping("/pedido/{idPedido}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getByPedidoId(@PathVariable String idPedido) {
        try {
            System.out.println("\n📥 ============== GET ITEMS BY PEDIDO ID ==============");
            System.out.println("📥 idPedido: " + idPedido);
            
            // ✅ Business se encarga de traer los items con JOIN FETCH
            var items = pedidoItemBusiness.getByPedidoId(idPedido);
            
            System.out.println("📦 Items encontrados: " + items.size());
            
            // ✅ Convertir entities a DTOs
            List<DtoPedidoItem> dtos = items.stream().map(entity -> {
                DtoPedidoItem dto = new DtoPedidoItem();
                dto.setIdItem(entity.getIdItem());
                dto.setIdProducto(entity.getProducto().getIdProducto());
                dto.setIdPedido(entity.getPedido().getIdPedido());
                dto.setCantidad(entity.getCantidad());
                dto.setPrecioUnitarioFinal(entity.getPrecioUnitarioFinal().doubleValue());
                
                // ✅ CRÍTICO: Incluir acompañamientos
                dto.setAcompanamientos(entity.getAcompanamientos());
                
                // Opcional: nombre del producto (sin imagen por ahora)
                dto.setNombreProducto(entity.getProducto().getNombre());
                // dto.setImagenProducto(entity.getProducto().getImagen()); // Comentar si no existe
                
                System.out.println("  → Item: " + entity.getIdItem());
                System.out.println("    - Acompañamientos: [" + entity.getAcompanamientos() + "]");
                
                return dto;
            }).collect(Collectors.toList());
            
            System.out.println("================================================\n");
            
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            System.err.println("❌ ERROR EN GET ITEMS: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(400).body(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * ✅ INSERTAR ITEM DE PEDIDO
     * POST: /pedidoItem/insert/{idPedido}
     */
    @PostMapping("/insert/{idPedido}")
    @Transactional
    public ResponseEntity<?> insert(
            @PathVariable String idPedido,
            @RequestBody DtoPedidoItem dtoPedidoItem) {
        try {
            System.out.println("\n📥 ============== INSERT ITEM ==============");
            System.out.println("📥 idPedido: " + idPedido);
            System.out.println("📥 Acompañamientos: [" + dtoPedidoItem.getAcompanamientos() + "]");
            
            // ✅ Insertar en business
            var resultado = pedidoItemBusiness.insert(idPedido, dtoPedidoItem);
            
            // ✅ Devolver DTO
            DtoPedidoItem dtoResponse = new DtoPedidoItem();
            dtoResponse.setIdItem(resultado.getIdItem());
            dtoResponse.setIdProducto(resultado.getProducto().getIdProducto());
            dtoResponse.setIdPedido(resultado.getPedido().getIdPedido());
            dtoResponse.setCantidad(resultado.getCantidad());
            dtoResponse.setPrecioUnitarioFinal(resultado.getPrecioUnitarioFinal().doubleValue());
            dtoResponse.setAcompanamientos(resultado.getAcompanamientos());
            
            System.out.println("✅ RESPUESTA: " + dtoResponse.getAcompanamientos());
            
            return ResponseEntity.ok(dtoResponse);
        } catch (Exception e) {
            System.err.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(400).body(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    public static class ErrorResponse {
        public String error;
        public ErrorResponse(String error) { this.error = error; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}