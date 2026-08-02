package com.epiis.app.entity;

import java.util.ArrayList;
import java.util.List;

import com.epiis.app.generic.EntityGeneric;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "pedido")
public class Pedido extends EntityGeneric {

    @Id
    @Column(name = "\"idPedido\"")
    private String idPedido;

    @ManyToOne(fetch = FetchType.LAZY) // 👈 Agregado LAZY
    @JoinColumn(name = "\"idEmpleado\"")
    private Empleado empleado;
    
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PedidoItem> items = new ArrayList<>();

    @Column(name = "\"nombreCliente\"")
    private String nombreCliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"tipoPedido\"")
    private TipoPedido tipoPedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"metodoPago\"")
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"estado\"")
    private EstadoPedido estado;

    @Column(name = "\"mesa\"")
    private Integer mesa;

    @Column(name = "\"totalPagar\"")
    private Double totalPagar;

    @Column(name = "\"motivoCancelacion\"")
    private String motivoCancelacion;

    @Column(name = "\"cancelacionRechazada\"")
    private Boolean cancelacionRechazada = false;

    public Boolean getCancelacionRechazada() {
        return cancelacionRechazada != null && cancelacionRechazada;
    }

    public enum TipoPedido {
        MESA,
        LLEVAR
    }

    public enum MetodoPago {
        EFECTIVO,
        YAPE
    }

    public enum EstadoPedido {
        ESPERA,
        PREPARACION,
        SOLICITUD_CANCELACION,
        CANCELADO,
        LISTO,
        FINALIZADO
    }
}