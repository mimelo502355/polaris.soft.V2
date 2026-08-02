package com.epiis.app.entity;

import com.epiis.app.generic.EntityGeneric;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "producto")
@Getter
@Setter
public class Producto extends EntityGeneric {

    @Id
    @Column(name = "\"idProducto\"")
    private String idProducto;

    @ManyToOne(fetch = FetchType.LAZY) // 👈 OBLIGATORIO LAZY
    @JoinColumn(name = "\"idCategoria\"")
    private Categoria categoria;

    @Column(name = "\"disponible\"")
    private Boolean disponible;

    @Column(name = "\"nombre\"")
    private String nombre;

    @Column(name = "\"precioBase\"")
    private Double precioBase;

    @Column(name = "\"descripcion\"")
    private String descripcion;

    @Column(name = "\"imagen_url\"") 
    private String imagenUrl;
}