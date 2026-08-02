package com.epiis.app.dataaccess;

import org.springframework.data.jpa.repository.JpaRepository;
import com.epiis.app.entity.Cliente;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, String> {

    Optional<Cliente> findByDni(String dni);

    Optional<Cliente> findByTelefono(String telefono);

    Optional<Cliente> findByDniAndEstado(String dni, Boolean estado);

    Optional<Cliente> findByEmail(String email);

    Optional<Cliente> findByDniAndEmail(String dni, String email);

    boolean existsByDni(String dni);

    boolean existsByTelefono(String telefono);

    boolean existsByEmail(String email);
}