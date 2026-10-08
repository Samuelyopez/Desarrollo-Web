package com.veterinaria.dogtor.service;

import java.util.List;

import com.veterinaria.dogtor.dto.DuenoRequest;
import com.veterinaria.dogtor.entities.Dueno;
import com.veterinaria.dogtor.entities.Mascota;

public interface DuenoService {

    // Todos los dueños, o solo los que coinciden con el texto (nombre, cédula, celular o correo)
    List<Dueno> listar(String buscar);

    Dueno buscarPorCedula(String cedula);

    List<Mascota> mascotasDe(String cedula);

    // Una mascota del dueño; 404 si no existe o es de otro dueño
    Mascota mascotaDe(String cedula, Long mascotaId);

    // Crea el dueño junto con su usuario (rol DUENO)
    Dueno crear(DuenoRequest request);

    // La cédula no se puede cambiar; la contraseña vacía se conserva
    Dueno actualizar(String cedula, DuenoRequest request);

    // Borra el dueño, sus mascotas y su usuario. Los tratamientos se conservan
    void eliminar(String cedula);
}
