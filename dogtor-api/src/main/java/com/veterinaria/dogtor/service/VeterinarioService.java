package com.veterinaria.dogtor.service;

import java.util.List;

import com.veterinaria.dogtor.dto.PacienteResponse;

public interface VeterinarioService {

    // Mascotas que ha tratado el veterinario (404 si no existe)
    List<PacienteResponse> pacientesDe(Long veterinarioId);
}
