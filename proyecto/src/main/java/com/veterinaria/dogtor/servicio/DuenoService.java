package com.veterinaria.dogtor.servicio;

import com.veterinaria.dogtor.entidad.Dueno;
import java.util.List;

public interface DuenoService {
    List<Dueno> searchAll();
    Dueno searchById(Long id);
    Dueno save(Dueno dueno);
    void delete(Long id);
    void toggleActivo(Long id);
    List<Dueno> searchAllIncluyendoInactivos();
}
