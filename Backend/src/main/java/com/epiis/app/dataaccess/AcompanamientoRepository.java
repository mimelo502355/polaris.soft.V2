package com.epiis.app.dataaccess;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.epiis.app.entity.Acompanamiento;

public interface AcompanamientoRepository extends JpaRepository<Acompanamiento, String> {
	List<Acompanamiento> findByTipoAcompanamiento(
	        Acompanamiento.TipoAcompanamiento tipo);
}
