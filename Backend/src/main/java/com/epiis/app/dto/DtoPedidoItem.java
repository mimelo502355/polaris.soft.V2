package com.epiis.app.dto;

import java.util.Date;
import java.util.List;

/**
 * ✅ DTO para PedidoItem
 * Se usa para evitar LazyLoading de Hibernate al serializar a JSON
 */
public class DtoPedidoItem {
    
    private String idItem;
    private String idProducto;
    private String idPedido;
    private Integer cantidad;
    private Double precioUnitarioFinal;
    private String acompanamientos;
    private List<String> toppings;
    private List<String> salsas;
    private List<String> toppingsIds;
    private List<String> salsasIds;
    private String nombreProducto;
    private String imagenProducto;
    private Date createdAt;
    private Date updatedAt;

    // ✅ CONSTRUCTORES
    public DtoPedidoItem() {
    }

    public DtoPedidoItem(String idItem, String idProducto, String idPedido,
                         Integer cantidad, Double precioUnitarioFinal) {
        this.idItem = idItem;
        this.idProducto = idProducto;
        this.idPedido = idPedido;
        this.cantidad = cantidad;
        this.precioUnitarioFinal = precioUnitarioFinal;
    }

    // ✅ GETTERS Y SETTERS
    public String getIdItem() {
        return idItem;
    }

    public void setIdItem(String idItem) {
        this.idItem = idItem;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecioUnitarioFinal() {
        return precioUnitarioFinal;
    }

    public void setPrecioUnitarioFinal(Double precioUnitarioFinal) {
        this.precioUnitarioFinal = precioUnitarioFinal;
    }

    public String getAcompanamientos() {
        return acompanamientos;
    }

    public void setAcompanamientos(String acompanamientos) {
        this.acompanamientos = acompanamientos;
    }

    public List<String> getToppings() {
        return toppings;
    }

    public void setToppings(List<String> toppings) {
        this.toppings = toppings;
    }

    public List<String> getSalsas() {
        return salsas;
    }

    public void setSalsas(List<String> salsas) {
        this.salsas = salsas;
    }

    public List<String> getToppingsIds() {
        return toppingsIds;
    }

    public void setToppingsIds(List<String> toppingsIds) {
        this.toppingsIds = toppingsIds;
    }

    public List<String> getSalsasIds() {
        return salsasIds;
    }

    public void setSalsasIds(List<String> salsasIds) {
        this.salsasIds = salsasIds;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getImagenProducto() {
        return imagenProducto;
    }

    public void setImagenProducto(String imagenProducto) {
        this.imagenProducto = imagenProducto;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "DtoPedidoItem{" +
                "idItem='" + idItem + '\'' +
                ", idProducto='" + idProducto + '\'' +
                ", idPedido='" + idPedido + '\'' +
                ", cantidad=" + cantidad +
                ", precioUnitarioFinal=" + precioUnitarioFinal +
                ", acompanamientos='" + acompanamientos + '\'' +
                ", toppings=" + toppings +
                ", salsas=" + salsas +
                '}';
    }
}