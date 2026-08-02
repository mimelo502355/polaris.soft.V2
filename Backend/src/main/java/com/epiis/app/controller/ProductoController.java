package com.epiis.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController; // 👈 IMPORTANTE: agregar esta importación
import org.springframework.web.multipart.MultipartFile;

import com.epiis.app.business.ProductoBusiness;
import com.epiis.app.controller.reqresp.RequestProductoInsert;
import com.epiis.app.dto.DtoProducto;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/producto")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductoController {

    private final ProductoBusiness productoBusiness;
    private final ObjectMapper objectMapper;

    public ProductoController(ProductoBusiness productoBusiness, ObjectMapper objectMapper) {
        this.productoBusiness = productoBusiness;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<List<DtoProducto>> getAll() {
        return ResponseEntity.ok(productoBusiness.getAll()); 
    }

    @GetMapping("/getall")
    public ResponseEntity<List<DtoProducto>> getAllAlias() {
        return ResponseEntity.ok(productoBusiness.getAll());
    }

    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<List<DtoProducto>> getByCategoria(@PathVariable String idCategoria) {
        return ResponseEntity.ok(productoBusiness.getByCategoria(idCategoria));
    }

    @PostMapping
    public ResponseEntity<DtoProducto> insert(@RequestBody RequestProductoInsert request) {
        DtoProducto dto = productoBusiness.insert(request.getProducto());
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateJson(@RequestBody DtoProducto dto) {
        try {
            DtoProducto actualizado = productoBusiness.updateConImagen(dto, null);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar producto: " + e.getMessage());
        }
    }

    @PostMapping(value = "/update-con-imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> updateConImagen(
        @RequestPart("producto") DtoProducto dto,
        @RequestPart(value = "imagen", required = false) MultipartFile file) {
    try {
        System.out.println(">>> DTO recibido correctamente: " + dto.getNombre());
        DtoProducto actualizado = productoBusiness.updateConImagen(dto, file);
        return ResponseEntity.ok(actualizado);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error al procesar el producto: " + e.getMessage());
    }
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        try {
            productoBusiness.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}