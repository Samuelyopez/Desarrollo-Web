package com.veterinaria.dogtor.servicio;

import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.entidad.RegistroMedico;
import java.util.List;

public interface RegistroMedicoService {
    List<RegistroMedico> findByMascota(Mascota mascota);
    RegistroMedico save(RegistroMedico registro);
}
