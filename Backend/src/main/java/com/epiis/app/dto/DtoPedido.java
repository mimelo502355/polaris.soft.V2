package com.epiis.app.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.epiis.app.generic.DtoGeneric;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoPedido extends DtoGeneric {
    private String idPedido;
    private String idEmpleado;
    private String nombreCliente;
    private String estado;
    private BigDecimal totalPagar;
    private String tipoPedido;
    private String metodoPago;
    private Integer mesa;
    private String motivoCancelacion;

    private List<DtoPedidoItemDetalle> items;

    // Formato explícito para Angular
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "America/Lima")
    private Date createdAt;
}