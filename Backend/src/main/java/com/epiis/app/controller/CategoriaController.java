package com.epiis.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.epiis.app.business.CategoriaBusiness;
import com.epiis.app.controller.reqresp.RequestCategoriaInsert;
import com.epiis.app.dto.DtoCategoria;

@RestController
@RequestMapping("/categoria")
public class CategoriaController {

    private final CategoriaBusiness categoriaBusiness;

    public CategoriaController(CategoriaBusiness categoriaBusiness) {
        this.categoriaBusiness = categoriaBusiness;
    }

    @GetMapping
    public ResponseEntity<List<DtoCategoria>> getAll() {
        return ResponseEntity.ok(categoriaBusiness.getAll());
    }

    @GetMapping("/getall")
    public ResponseEntity<List<DtoCategoria>> getAllAngular() {
        return ResponseEntity.ok(categoriaBusiness.getAll());
    }

    @PostMapping
    public ResponseEntity<DtoCategoria> insert(
            @RequestBody RequestCategoriaInsert request) {

        DtoCategoria dto = categoriaBusiness.insert(request.getDto());
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }
}
