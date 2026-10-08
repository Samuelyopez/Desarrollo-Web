package com.veterinaria.dogtor.dto;

import java.time.LocalDate;

// Fila de "Mis pacientes": una mascota que el veterinario ha tratado, cuántas veces y cuándo fue la última
public record PacienteResponse(
        Long mascotaId,
        String nombre,
        String raza,
        String foto,
        boolean activa,
        String duenoNombre,
        String duenoCedula,
        Long cantidadTratamientos,
        LocalDate ultimoTratamiento) {
}
