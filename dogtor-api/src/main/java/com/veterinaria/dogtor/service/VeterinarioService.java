package com.veterinaria.dogtor.service;

import java.util.List;

import com.veterinaria.dogtor.dto.PacienteResponse;
import com.veterinaria.dogtor.dto.VeterinarioRequest;
import com.veterinaria.dogtor.entities.Veterinario;

public interface VeterinarioService {

    // Todos los veterinarios o los que coinciden con el texto; activo null = cualquier estado
    List<Veterinario> listar(String buscar, Boolean activo);

    Veterinario buscarPorCedula(String cedula);

    // Crea el veterinario junto con su usuario (rol VETERINARIO, activo)
    Veterinario crear(VeterinarioRequest request);

    // La cédula no se puede cambiar; la contraseña vacía se conserva; el estado no se toca
    Veterinario actualizar(String cedula, VeterinarioRequest request);

    // Activa o desactiva (estado laboral). No se eliminan veterinarios
    Veterinario cambiarEstado(String cedula, boolean activo);

    // Mascotas que ha tratado el veterinario (404 si no existe)
    List<PacienteResponse> pacientesDe(Long veterinarioId);
}
