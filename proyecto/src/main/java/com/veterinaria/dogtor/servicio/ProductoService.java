package com.veterinaria.dogtor.servicio;

import com.veterinaria.dogtor.entidad.Producto;
import java.util.List;

public interface ProductoService {
    List<Producto> searchAll();
    List<Producto> findByCategoria(com.veterinaria.dogtor.entidad.CategoriaProducto categoria);
    Producto save(Producto producto);
    Producto searchById(Long id);
}

