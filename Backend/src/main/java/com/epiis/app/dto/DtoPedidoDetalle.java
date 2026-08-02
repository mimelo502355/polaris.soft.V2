package com.epiis.app.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoPedidoDetalle {
    private String idPedido;
    private String nombreCliente;
    private String estado;
    private String tipoPedido;
    private String metodoPago;
    private Integer mesa;
    private BigDecimal totalPagar;
    private List<DtoPedidoItemDetalle> items;
}
