package com.epiis.app.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.epiis.app.generic.EntityGeneric;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pedidoItem")
@Getter
@Setter
public class PedidoItem extends EntityGeneric {

    @Id
    @Column(name = "\"idItem\"")
    private String idItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"idProducto\"")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"idPedido\"")
    @JsonIgnore
    private Pedido pedido;

    @Column(name = "\"cantidad\"")
    private int cantidad;

    @Column(name = "\"precioUnitarioFinal\"")
    private BigDecimal precioUnitarioFinal;

    @Column(name = "\"createdAt\"")
    private Timestamp createdAt;

    @Column(name = "\"updatedAt\"")
    private Timestamp updatedAt;

    @Column(name = "\"acompanamientos\"", columnDefinition = "TEXT")
    private String acompanamientos;
}