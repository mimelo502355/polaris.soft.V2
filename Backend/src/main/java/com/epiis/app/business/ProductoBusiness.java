package com.epiis.app.business;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.epiis.app.dataaccess.CategoriaRepository;
import com.epiis.app.dataaccess.ProductoRepository;
import com.epiis.app.dto.DtoProducto;
import com.epiis.app.entity.Categoria;
import com.epiis.app.entity.Producto;
import com.epiis.app.util.StringUtils;

@Service
public class ProductoBusiness {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    @Value("${app.upload.dir}")
    private String baseUploadDir;

    public ProductoBusiness(ProductoRepository productoRepository,
                            CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true) // 👈 AGREGADO
    public List<DtoProducto> getAll() {
        List<Producto> productos = productoRepository.findAllWithCategoria();
        List<DtoProducto> dtos = new ArrayList<>();

        for (Producto p : productos) {
            dtos.add(convertirEntidadADto(p));
        }
        return dtos;
    }

    @Transactional(readOnly = true) // 👈 AGREGADO
    public DtoProducto getById(String idProducto) {
        Producto p = productoRepository.findById(idProducto).orElse(null);
        return p != null ? convertirEntidadADto(p) : null;
    }

    @Transactional(readOnly = true) // 👈 AGREGADO
    public List<DtoProducto> getByCategoria(String idCategoria) {
        List<Producto> productos = productoRepository.findByCategoriaWithFetch(idCategoria);
        List<DtoProducto> dtos = new ArrayList<>();

        for (Producto p : productos) {
            dtos.add(convertirEntidadADto(p));
        }
        return dtos;
    }

    @Transactional // 👈 AGREGADO
    public DtoProducto insert(DtoProducto dto) {
        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + dto.getIdCategoria()));

        Producto producto = new Producto();
        producto.setIdProducto(UUID.randomUUID().toString());
        producto.setCategoria(categoria);
        producto.setNombre(dto.getNombre());
        producto.setDisponible(dto.getDisponible() != null ? dto.getDisponible() : true);
        producto.setPrecioBase(dto.getPrecioBase());
        producto.setDescripcion(dto.getDescripcion());
        producto.setImagenUrl(dto.getImagenUrl());
        producto.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        producto.setUpdatedAt(producto.getCreatedAt());

        productoRepository.save(producto);

        dto.setIdProducto(producto.getIdProducto());
        return dto;
    }

    @Transactional // 👈 AGREGADO
    public DtoProducto updateConImagen(DtoProducto dto, MultipartFile file) throws IOException {
        Producto producto;
        boolean esNuevo = false;

        if (dto.getIdProducto() != null && !dto.getIdProducto().trim().isEmpty()) {
            producto = productoRepository.findById(dto.getIdProducto()).orElse(null);
            if (producto == null) {
                producto = new Producto();
                producto.setIdProducto(UUID.randomUUID().toString());
                producto.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                esNuevo = true;
            }
        } else {
            producto = new Producto();
            producto.setIdProducto(UUID.randomUUID().toString());
            producto.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            esNuevo = true;
        }

        Categoria categoria = null;
        if (dto.getIdCategoria() != null) {
            categoria = categoriaRepository.findById(dto.getIdCategoria()).orElse(null);
            producto.setCategoria(categoria);
        }

        if (file != null && !file.isEmpty()) {

            if (!esNuevo && producto.getImagenUrl() != null && !producto.getImagenUrl().trim().isEmpty()) {
                borrarArchivoFisico(producto.getImagenUrl());
            }

            String nombreCategoria = (categoria != null && categoria.getNombre() != null)
                    ? categoria.getNombre()
                    : "varios";

            String folderCategoria = StringUtils.toKebabCase(nombreCategoria);
            String nameProducto = StringUtils.toKebabCase(dto.getNombre());

            String originalFilename = file.getOriginalFilename();
            String extension = ".png";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            }

            String baseDir = baseUploadDir.endsWith(File.separator) || baseUploadDir.endsWith("/") 
                    ? baseUploadDir 
                    : baseUploadDir + File.separator;

            File carpetaDestino = new File(baseDir + folderCategoria);
            if (!carpetaDestino.exists()) {
                carpetaDestino.mkdirs();
            }

            String nombreArchivoFinal = nameProducto + "_" + System.currentTimeMillis() + extension;
            Path rutaCompleta = Paths.get(carpetaDestino.getAbsolutePath(), nombreArchivoFinal);

            Files.write(rutaCompleta, file.getBytes());

            dto.setImagenUrl("/img/" + folderCategoria + "/" + nombreArchivoFinal);
        } else if (!esNuevo) {
            dto.setImagenUrl(producto.getImagenUrl());
        }

        producto.setNombre(dto.getNombre());
        producto.setDisponible(dto.getDisponible() != null ? dto.getDisponible() : true);
        producto.setPrecioBase(dto.getPrecioBase());
        producto.setDescripcion(dto.getDescripcion());
        producto.setImagenUrl(dto.getImagenUrl());
        producto.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        productoRepository.save(producto);

        dto.setIdProducto(producto.getIdProducto());
        return dto;
    }

    @Transactional // 👈 AGREGADO
    public void delete(String idProducto) {
        Producto producto = productoRepository.findById(idProducto).orElse(null);

        if (producto != null) {
            if (producto.getImagenUrl() != null && !producto.getImagenUrl().trim().isEmpty()) {
                borrarArchivoFisico(producto.getImagenUrl());
            }
            productoRepository.deleteById(idProducto);
        }
    }

    private void borrarArchivoFisico(String imagenUrl) {
        try {
            if (imagenUrl == null || imagenUrl.trim().isEmpty()) return;

            String rutaRelativa = imagenUrl;
            if (rutaRelativa.startsWith("/img/")) {
                rutaRelativa = rutaRelativa.substring(5);
            } else if (rutaRelativa.startsWith("/")) {
                rutaRelativa = rutaRelativa.substring(1);
            }

            Path rutaAbsoluta = Paths.get(baseUploadDir).resolve(rutaRelativa).normalize();
            Files.deleteIfExists(rutaAbsoluta);
        } catch (IOException e) {
            System.err.println("⚠️ No se pudo eliminar la imagen del disco: " + e.getMessage());
        }
    }

    private DtoProducto convertirEntidadADto(Producto p) {
        DtoProducto dto = new DtoProducto();
        dto.setIdProducto(p.getIdProducto());
        dto.setIdCategoria(p.getCategoria() != null ? p.getCategoria().getIdCategoria() : null);
        dto.setNombre(p.getNombre());
        dto.setDisponible(p.getDisponible());
        dto.setPrecioBase(p.getPrecioBase());
        dto.setDescripcion(p.getDescripcion());
        dto.setImagenUrl(p.getImagenUrl());
        dto.setCreatedAt(p.getCreatedAt() != null ? new Date(p.getCreatedAt().getTime()) : null);
        dto.setUpdatedAt(p.getUpdatedAt() != null ? new Date(p.getUpdatedAt().getTime()) : null);
        return dto;
    }
}