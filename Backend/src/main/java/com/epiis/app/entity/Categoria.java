package com.epiis.app.entity;

import com.epiis.app.generic.EntityGeneric;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categoria")
@Getter
@Setter
public class Categoria extends EntityGeneric {

    @Id
    @Column(name = "\"idCategoria\"")
    private String idCategoria;

    @Column(name = "\"nombre\"")
    private String nombre;
}