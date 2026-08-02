package com.epiis.app.dataaccess;

import org.springframework.data.jpa.repository.JpaRepository;
import com.epiis.app.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, String> {}
