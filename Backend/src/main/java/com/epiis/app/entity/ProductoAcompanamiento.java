package com.epiis.app.entity;

import com.epiis.app.generic.EntityGeneric;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "productoAcompanamiento")
@Getter
@Setter
public class ProductoAcompanamiento extends EntityGeneric {

    @Id
    @Column(name = "\"idProdAcomp\"")
    private String idProdAcomp;

    @ManyToOne
    @JoinColumn(name = "\"idProducto\"")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "\"idAcompanamiento\"")
    private Acompanamiento acompanamiento;
}