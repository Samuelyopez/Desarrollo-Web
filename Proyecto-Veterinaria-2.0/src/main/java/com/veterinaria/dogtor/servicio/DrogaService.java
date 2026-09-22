package com.veterinaria.dogtor.servicio;

import com.veterinaria.dogtor.entidad.Droga;
import java.util.List;

public interface DrogaService {
    List<Droga> findAll();
    Droga findById(Integer id);
    Droga save(Droga droga);
}
