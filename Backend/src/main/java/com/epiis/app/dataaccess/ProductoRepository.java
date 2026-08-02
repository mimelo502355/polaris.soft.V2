package com.epiis.app.dataaccess;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.epiis.app.entity.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, String> {
    
    // 🟢 OPTIMIZADO: Carga productos y sus categorías juntas en 1 sola consulta
    @Query("SELECT DISTINCT p FROM Producto p LEFT JOIN FETCH p.categoria")
    List<Producto> findAllWithCategoria();

    // 🟢 OPTIMIZADO: Carga productos por categoría en 1 sola consulta
    @Query("SELECT DISTINCT p FROM Producto p LEFT JOIN FETCH p.categoria WHERE p.categoria.idCategoria = :idCategoria")
    List<Producto> findByCategoriaWithFetch(@Param("idCategoria") String idCategoria);

    List<Producto> findByCategoria_IdCategoria(String idCategoria);
}