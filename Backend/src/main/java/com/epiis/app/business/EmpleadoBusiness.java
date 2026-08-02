package com.epiis.app.business;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epiis.app.dataaccess.EmpleadoRepository;
import com.epiis.app.dto.DtoEmpleado;
import com.epiis.app.entity.Empleado;

@Service
public class EmpleadoBusiness {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    public boolean insert(DtoEmpleado dto) {
        // 🚨 VALIDACIÓN ANTIDUPLICADOS: Evita guardar dos empleados con el mismo nombre
        if (dto.getNombre() != null && empleadoRepository.existsByNombre(dto.getNombre().trim())) {
            throw new IllegalArgumentException("Ya existe un empleado registrado con el nombre: " + dto.getNombre());
        }

        Empleado empleado = new Empleado();
        empleado.setIdEmpleado(UUID.randomUUID().toString());
        empleado.setNombre(dto.getNombre().trim());
        empleado.setPassword(dto.getPassword());
        empleado.setCreatedAt(new Timestamp(new Date().getTime()));
        empleado.setUpdatedAt(new Timestamp(new Date().getTime()));

        empleadoRepository.save(empleado);
        return true;
    }

    public List<DtoEmpleado> getAll() {
        List<Empleado> empleados = empleadoRepository.findAll();
        List<DtoEmpleado> dtos = new ArrayList<>();

        for (Empleado e : empleados) {
            DtoEmpleado dto = new DtoEmpleado();
            dto.setIdEmpleado(e.getIdEmpleado());
            dto.setNombre(e.getNombre());
            dto.setPassword(e.getPassword());
            dto.setCreatedAt(new Date(e.getCreatedAt().getTime()));
            dto.setUpdatedAt(new Date(e.getUpdatedAt().getTime()));
            dtos.add(dto);
        }

        return dtos;
    }
}