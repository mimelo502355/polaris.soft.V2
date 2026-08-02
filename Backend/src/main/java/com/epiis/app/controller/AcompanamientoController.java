package com.epiis.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.epiis.app.business.AcompanamientoBusiness;
import com.epiis.app.controller.reqresp.RequestAcompanamientoInsert;
import com.epiis.app.controller.reqresp.ResponseAcompanamientoInsert;
import com.epiis.app.dto.DtoAcompanamiento;
import com.epiis.app.entity.Acompanamiento;

@RestController
@RequestMapping("acompanamiento")
public class AcompanamientoController {

    @Autowired
    private AcompanamientoBusiness acompanamientoBusiness;

    @PostMapping(path = "insert", consumes = "application/json")
    public ResponseEntity<ResponseAcompanamientoInsert> insert(
            @RequestBody RequestAcompanamientoInsert request) {

        DtoAcompanamiento creado =
                acompanamientoBusiness.insert(request.getDto());

        ResponseAcompanamientoInsert response = new ResponseAcompanamientoInsert();
        response.success();
        response.setAcompanamiento(creado);
        response.getListMessage().add("Acompañamiento registrado correctamente");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(path = "getall")
    public ResponseEntity<List<DtoAcompanamiento>> getAll() {
        return ResponseEntity.ok(acompanamientoBusiness.getAll());
    }
    @GetMapping(path = "topping")
    public ResponseEntity<List<DtoAcompanamiento>> getTopping() {
        return ResponseEntity.ok(
            acompanamientoBusiness.getByTipo(
                Acompanamiento.TipoAcompanamiento.TOPPING
            )
        );
    }

    @GetMapping(path = "salsas")
    public ResponseEntity<List<DtoAcompanamiento>> getSalsas() {
        return ResponseEntity.ok(
            acompanamientoBusiness.getByTipo(
                Acompanamiento.TipoAcompanamiento.SALSA
            )
        );
    }

}
