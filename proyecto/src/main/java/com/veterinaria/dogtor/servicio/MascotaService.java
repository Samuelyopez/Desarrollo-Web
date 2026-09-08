package com.veterinaria.dogtor.servicio;

import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.entidad.Dueno;
import java.util.List;

public interface MascotaService {
    List<Mascota> searchAll();
    Mascota searchById(Long id);
    List<Mascota> findByDueno(Dueno dueno);
    List<Mascota> findByEnAdopcion(boolean enAdopcion);
    List<Mascota> searchAllIncluyendoInactivas();
    Mascota save(Mascota mascota);
    void delete(Long id);
    void toggleActivo(Long id);
}

