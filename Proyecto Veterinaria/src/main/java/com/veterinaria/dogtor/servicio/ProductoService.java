package com.veterinaria.dogtor.servicio;

import com.veterinaria.dogtor.entidad.Producto;
import java.util.List;

public interface ProductoService {
    List<Producto> findAll();
    List<Producto> findByCategoria(String categoria);
    Producto save(Producto producto);
}
