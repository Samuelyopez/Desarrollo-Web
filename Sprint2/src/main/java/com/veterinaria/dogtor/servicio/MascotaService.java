package com.veterinaria.dogtor.servicio;

import com.veterinaria.dogtor.entidad.Mascota;
import java.util.List;

public interface MascotaService {
    List<Mascota> findAll();
    Mascota findById(Integer id);
    Mascota save(Mascota mascota);
    void delete(Integer id);
}
