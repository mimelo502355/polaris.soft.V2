package com.epiis.app.controller.reqresp;

import com.epiis.app.dto.DtoPedido;
import lombok.Getter;
import lombok.Setter;

public class RequestPedidoInsert {

    private DtoPedido dto;

    public DtoPedido getDto() {
        return dto;
    }

    public void setDto(DtoPedido dto) {
        this.dto = dto;
    }
}

