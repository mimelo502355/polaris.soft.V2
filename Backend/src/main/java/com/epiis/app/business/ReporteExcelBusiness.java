package com.epiis.app.business;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epiis.app.dataaccess.EmpleadoRepository;
import com.epiis.app.dataaccess.PedidoRepository;
import com.epiis.app.dataaccess.ProductoRepository;
import com.epiis.app.entity.Empleado;
import com.epiis.app.entity.Pedido;
import com.epiis.app.entity.Producto;

@Service
public class ReporteExcelBusiness {

    private final PedidoRepository pedidoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ProductoRepository productoRepository;

    @Autowired
    public ReporteExcelBusiness(PedidoRepository pedidoRepository,
                                EmpleadoRepository empleadoRepository,
                                ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.empleadoRepository = empleadoRepository;
        this.productoRepository = productoRepository;
    }

    private CellStyle crearEstiloHeader(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 11);

        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    // 1. REPORTE DE EMPLEADOS
    public ByteArrayInputStream generarReporteEmpleados() throws IOException {
        String[] columnas = {"ID Empleado", "Nombre", "Fecha de Creación"};
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Empleados");
            CellStyle headerStyle = crearEstiloHeader(workbook);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            List<Empleado> empleados = empleadoRepository.findAll();
            int rowIdx = 1;
            for (Empleado e : empleados) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(e.getIdEmpleado() != null ? e.getIdEmpleado() : "");
                row.createCell(1).setCellValue(e.getNombre() != null ? e.getNombre() : "");
                row.createCell(2).setCellValue(e.getCreatedAt() != null ? e.getCreatedAt().toString() : "");
            }

            for (int i = 0; i < columnas.length; i++) sheet.autoSizeColumn(i);
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    // 2. REPORTE DE PRODUCTOS
    public ByteArrayInputStream generarReporteProductos() throws IOException {
        String[] columnas = {"ID Producto", "Nombre", "Categoría", "Precio Base (S/.)", "Estado", "Descripción"};
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Productos");
            CellStyle headerStyle = crearEstiloHeader(workbook);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            List<Producto> productos = productoRepository.findAll();
            int rowIdx = 1;
            for (Producto p : productos) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(p.getIdProducto() != null ? p.getIdProducto() : "");
                row.createCell(1).setCellValue(p.getNombre() != null ? p.getNombre() : "");
                row.createCell(2).setCellValue(p.getCategoria() != null ? p.getCategoria().getNombre() : "Sin Categoría");
                row.createCell(3).setCellValue(p.getPrecioBase() != null ? p.getPrecioBase() : 0.0);
                row.createCell(4).setCellValue(p.getDisponible() != null && p.getDisponible() ? "DISPONIBLE" : "NO DISPONIBLE");
                row.createCell(5).setCellValue(p.getDescripcion() != null ? p.getDescripcion() : "");
            }

            for (int i = 0; i < columnas.length; i++) sheet.autoSizeColumn(i);
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    // 3. REPORTE DE VENTAS / PEDIDOS
    public ByteArrayInputStream generarReporteVentas() throws IOException {
        String[] columnas = {"ID Pedido", "Fecha Creación"};
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Ventas");
            CellStyle headerStyle = crearEstiloHeader(workbook);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            List<Pedido> pedidos = pedidoRepository.findAll();
            int rowIdx = 1;
            for (Pedido p : pedidos) {
                Row row = sheet.createRow(rowIdx++);
                // Asumiendo que Pedido sigue la convención idPedido y la relación heredada
                row.createCell(0).setCellValue(p.getIdPedido() != null ? p.getIdPedido() : "");
                row.createCell(1).setCellValue(p.getCreatedAt() != null ? p.getCreatedAt().toString() : "");
            }

            for (int i = 0; i < columnas.length; i++) sheet.autoSizeColumn(i);
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}