package com.veterinaria.dogtor.repositorio;

import com.veterinaria.dogtor.entidad.Producto;
import com.veterinaria.dogtor.entidad.CategoriaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {



    List<Producto> findByCategoria(CategoriaProducto categoria);
}
