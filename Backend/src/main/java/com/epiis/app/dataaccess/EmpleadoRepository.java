package com.epiis.app.dataaccess;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epiis.app.entity.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, String> {

    // Soluciona el error en EmpleadoController (espera la entidad Empleado directamente)
    Empleado findByNombre(String nombre);

    // Soluciona el error en EmpleadoBusiness (valida si existe el registro)
    boolean existsByNombre(String nombre);

}