package com.epiis.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.epiis.app.business.ProductoAcompanamientoBusiness;
import com.epiis.app.controller.reqresp.RequestProductoAcompanamientoInsert;
import com.epiis.app.controller.reqresp.ResponseProductoAcompanamientoInsert;
import com.epiis.app.dto.DtoProductoAcompanamiento;
import com.epiis.app.dto.DtoAcompanamiento;

@RestController
@RequestMapping(path = "producto-acompanamiento")
public class ProductoAcompanamientoController {

    @Autowired
    private ProductoAcompanamientoBusiness productoAcompanamientoBusiness;

    // 🔹 Insertar acompañamiento a un producto
    @PostMapping(path = "insert", consumes = "application/json")
    public ResponseEntity<ResponseProductoAcompanamientoInsert> insert(
            @RequestBody RequestProductoAcompanamientoInsert request) {

        ResponseProductoAcompanamientoInsert response =
                new ResponseProductoAcompanamientoInsert();

        productoAcompanamientoBusiness.insert(
                request.getProductoAcompanamiento()
        );

        response.success();
        response.getListMessage()
                .add("Acompañamiento asignado al producto correctamente");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 🔹 Obtener todos los registros
    @GetMapping(path = "getall")
    public ResponseEntity<List<DtoProductoAcompanamiento>> getAll() {
        return new ResponseEntity<>(
                productoAcompanamientoBusiness.getAll(),
                HttpStatus.OK
        );
    }

    // 🔹 Obtener acompañamientos de un producto específico (con nombre y tipo)
    @GetMapping(path = "getbyproducto/{idProducto}")
    public ResponseEntity<List<DtoAcompanamiento>> getByProducto(@PathVariable String idProducto) {
        List<DtoAcompanamiento> list = productoAcompanamientoBusiness.getByProductoFull(idProducto);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
