package com.epiis.app.dataaccess;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epiis.app.entity.ProductoAcompanamiento;

public interface ProductoAcompanamientoRepository extends JpaRepository<ProductoAcompanamiento, String> {
	
	@Query("SELECT pa FROM ProductoAcompanamiento pa WHERE pa.producto.idProducto = :idProducto")
    List<ProductoAcompanamiento> findByProductoId(@Param("idProducto") String idProducto);

}
