package com.epiis.app.controller.reqresp;

import com.epiis.app.dto.DtoCategoria;
import com.epiis.app.generic.ResponseGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseCategoriaInsert extends ResponseGeneric {

    private DtoCategoria categoria;
}
