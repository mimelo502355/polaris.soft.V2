package com.epiis.app.controller.reqresp;

import com.epiis.app.dto.DtoAcompanamiento;
import com.epiis.app.generic.ResponseGeneric;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseAcompanamientoInsert extends ResponseGeneric {

    private DtoAcompanamiento acompanamiento;

}
