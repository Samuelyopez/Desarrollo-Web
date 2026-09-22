package com.veterinaria.dogtor.servicio;

import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.entidad.Dueno;
import java.util.List;

public interface MascotaService {
    List<Mascota> findAll();
    Mascota findById(Integer id);
    List<Mascota> findByDueno(Dueno dueno);
    List<Mascota> findByEnAdopcion(boolean enAdopcion);
    List<Mascota> findByNombreContaining(String nombre);
    Mascota save(Mascota mascota);
}
