package com.epiis.app.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epiis.app.business.ReporteExcelBusiness;

@RestController
@RequestMapping("/reportes")
@CrossOrigin(origins = "*") // Permite peticiones desde Angular
public class ReporteController {

    private final ReporteExcelBusiness reporteExcelBusiness;

    @Autowired
    public ReporteController(ReporteExcelBusiness reporteExcelBusiness) {
        this.reporteExcelBusiness = reporteExcelBusiness;
    }

    @GetMapping("/excel/empleados")
    public ResponseEntity<InputStreamResource> exportarEmpleados() throws IOException {
        ByteArrayInputStream stream = reporteExcelBusiness.generarReporteEmpleados();
        return crearResponseExcel(stream, "Reporte_Empleados.xlsx");
    }

    @GetMapping("/excel/productos")
    public ResponseEntity<InputStreamResource> exportarProductos() throws IOException {
        ByteArrayInputStream stream = reporteExcelBusiness.generarReporteProductos();
        return crearResponseExcel(stream, "Reporte_Productos.xlsx");
    }

    @GetMapping("/excel/ventas")
    public ResponseEntity<InputStreamResource> exportarVentas() throws IOException {
        ByteArrayInputStream stream = reporteExcelBusiness.generarReporteVentas();
        return crearResponseExcel(stream, "Reporte_Ventas.xlsx");
    }

    private ResponseEntity<InputStreamResource> crearResponseExcel(ByteArrayInputStream stream, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + filename);
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(stream));
    }
}