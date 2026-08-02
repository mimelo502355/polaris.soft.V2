package com.epiis.app.dto;

import com.epiis.app.generic.DtoGeneric;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoCategoria extends DtoGeneric {

    private String idCategoria;
    private String nombre;
}
