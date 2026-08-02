package com.epiis.app.entity;

import com.epiis.app.generic.EntityGeneric;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "acompanamiento")
@Getter
@Setter
public class Acompanamiento extends EntityGeneric {

    @Id
    @Column(name = "\"idAcompanamiento\"")
    private String idAcompanamiento;

    @Column(name = "\"nombreAcompanamiento\"")
    private String nombreAcompanamiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"tipoAcompanamiento\"")
    private TipoAcompanamiento tipoAcompanamiento;

    // ✅ ENUM ANIDADA
    public enum TipoAcompanamiento {
        SALSA,
        TOPPING,
        BEBIDA
    }
}