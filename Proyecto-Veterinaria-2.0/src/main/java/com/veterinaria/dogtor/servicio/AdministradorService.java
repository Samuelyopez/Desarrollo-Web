package com.veterinaria.dogtor.servicio;

import com.veterinaria.dogtor.entidad.Administrador;
import java.util.List;

public interface AdministradorService {
    List<Administrador> findAll();
    Administrador save(Administrador administrador);
}
