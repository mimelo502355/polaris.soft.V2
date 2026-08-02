package com.epiis.app.business;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.epiis.app.dataaccess.CategoriaRepository;
import com.epiis.app.dto.DtoCategoria;
import com.epiis.app.entity.Categoria;

import jakarta.transaction.Transactional;

@Service
public class CategoriaBusiness {

    private final CategoriaRepository categoriaRepository;

    public CategoriaBusiness(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public DtoCategoria insert(DtoCategoria dto) {

        if (dto == null || dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }

        Categoria entity = new Categoria();
        entity.setIdCategoria(UUID.randomUUID().toString());
        entity.setNombre(dto.getNombre());
        entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        entity.setUpdatedAt(entity.getCreatedAt());

        categoriaRepository.save(entity);

        dto.setIdCategoria(entity.getIdCategoria());
        dto.setCreatedAt(new Date(entity.getCreatedAt().getTime()));
        dto.setUpdatedAt(new Date(entity.getUpdatedAt().getTime()));

        return dto;
    }

    public List<DtoCategoria> getAll() {

        return categoriaRepository.findAll().stream().map(c -> {
            DtoCategoria dto = new DtoCategoria();
            dto.setIdCategoria(c.getIdCategoria());
            dto.setNombre(c.getNombre());
            dto.setCreatedAt(new Date(c.getCreatedAt().getTime()));
            dto.setUpdatedAt(new Date(c.getUpdatedAt().getTime()));
            return dto;
        }).toList();
    }
}
