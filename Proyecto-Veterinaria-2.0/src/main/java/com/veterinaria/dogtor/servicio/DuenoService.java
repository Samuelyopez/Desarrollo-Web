package com.veterinaria.dogtor.servicio;

import com.veterinaria.dogtor.entidad.Dueno;
import java.util.List;

public interface DuenoService {
    List<Dueno> findAll();
    Dueno findById(Integer id);
    Dueno findByCorreo(String correo);
    List<Dueno> findByNombreContaining(String nombre);
    Dueno save(Dueno dueno);
    void delete(Integer id);
}
