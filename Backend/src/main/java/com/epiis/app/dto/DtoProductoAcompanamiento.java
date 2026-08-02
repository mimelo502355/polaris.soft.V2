package com.epiis.app.dto;

import com.epiis.app.generic.DtoGeneric;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoProductoAcompanamiento extends DtoGeneric {
    private String idProdAcomp;
    private String idProducto;      
    private String idAcompanamiento; 
}
