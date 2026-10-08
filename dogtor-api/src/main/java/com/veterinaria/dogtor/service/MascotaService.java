package com.veterinaria.dogtor.service;

import java.util.List;

import com.veterinaria.dogtor.dto.MascotaRequest;
import com.veterinaria.dogtor.entities.Mascota;

// Las mascotas no se eliminan: solo se activan (en la clínica) o desactivan (en casa).
// Únicamente desaparecen cuando se elimina su dueño
public interface MascotaService {

    // Todas las mascotas, o solo las que coinciden con el texto (nombre, raza o dueño)
    List<Mascota> listar(String buscar);

    Mascota buscarPorId(Long id);

    // Crea la mascota activa para el dueño de la cédula indicada
    Mascota crear(MascotaRequest request);

    // Actualiza los datos (incluido el dueño) sin tocar el estado
    Mascota actualizar(Long id, MascotaRequest request);

    Mascota cambiarEstado(Long id, boolean activa);
}
