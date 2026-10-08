package com.veterinaria.dogtor.service;

import java.util.List;

import com.veterinaria.dogtor.dto.TratamientoRequest;
import com.veterinaria.dogtor.entities.Tratamiento;

public interface TratamientoService {

    // Tratamientos de una mascota, del más reciente al más antiguo (404 si la mascota no existe)
    List<Tratamiento> historialDe(Long mascotaId);

    // Da el medicamento a una mascota activa con la fecha de hoy y descuenta las unidades
    Tratamiento crear(TratamientoRequest request);
}
