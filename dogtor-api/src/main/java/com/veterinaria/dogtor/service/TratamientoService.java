package com.veterinaria.dogtor.service;

import java.util.List;

import com.veterinaria.dogtor.entities.Tratamiento;

public interface TratamientoService {

    // Tratamientos de una mascota, del más reciente al más antiguo (404 si la mascota no existe)
    List<Tratamiento> historialDe(Long mascotaId);
}
