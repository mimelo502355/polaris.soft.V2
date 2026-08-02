package com.epiis.app.business;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epiis.app.dataaccess.AcompanamientoRepository;
import com.epiis.app.dto.DtoAcompanamiento;
import com.epiis.app.entity.Acompanamiento;

@Service
public class AcompanamientoBusiness {

    @Autowired
    private AcompanamientoRepository acompanamientoRepository;

    public DtoAcompanamiento insert(DtoAcompanamiento dto) {

        dto.setIdAcompanamiento(UUID.randomUUID().toString());
        dto.setCreatedAt(new Date());
        dto.setUpdatedAt(dto.getCreatedAt());

        Acompanamiento entity = new Acompanamiento();
        entity.setIdAcompanamiento(dto.getIdAcompanamiento());
        entity.setNombreAcompanamiento(dto.getNombreAcompanamiento());
        entity.setTipoAcompanamiento(
                Acompanamiento.TipoAcompanamiento.valueOf(dto.getTipoAcompanamiento())
        );
        entity.setCreatedAt(new Timestamp(dto.getCreatedAt().getTime()));
        entity.setUpdatedAt(new Timestamp(dto.getUpdatedAt().getTime()));

        acompanamientoRepository.save(entity);

        return dto;
    }

    public List<DtoAcompanamiento> getAll() {

        List<Acompanamiento> list = acompanamientoRepository.findAll();
        List<DtoAcompanamiento> dtos = new ArrayList<>();

        for (Acompanamiento a : list) {
            DtoAcompanamiento dto = new DtoAcompanamiento();
            dto.setIdAcompanamiento(a.getIdAcompanamiento());
            dto.setNombreAcompanamiento(a.getNombreAcompanamiento());
            dto.setTipoAcompanamiento(a.getTipoAcompanamiento().name());
            dto.setCreatedAt(new Date(a.getCreatedAt().getTime()));
            dto.setUpdatedAt(new Date(a.getUpdatedAt().getTime()));

            dtos.add(dto);
        }

        return dtos;
    }
    public List<DtoAcompanamiento> getByTipo(
            Acompanamiento.TipoAcompanamiento tipo) {

        List<Acompanamiento> list =
                acompanamientoRepository.findByTipoAcompanamiento(tipo);

        List<DtoAcompanamiento> dtos = new ArrayList<>();

        for (Acompanamiento a : list) {
            DtoAcompanamiento dto = new DtoAcompanamiento();
            dto.setIdAcompanamiento(a.getIdAcompanamiento());
            dto.setNombreAcompanamiento(a.getNombreAcompanamiento());
            dto.setTipoAcompanamiento(a.getTipoAcompanamiento().name());
            dto.setCreatedAt(new Date(a.getCreatedAt().getTime()));
            dto.setUpdatedAt(new Date(a.getUpdatedAt().getTime()));
            dtos.add(dto);
        }

        return dtos;
    }

}
