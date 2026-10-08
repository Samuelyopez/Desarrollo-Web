package com.veterinaria.dogtor.dto;

import jakarta.validation.constraints.NotNull;

// Cuerpo de PUT /api/veterinarios/{cedula}/estado. Es un estado laboral (vacaciones, incapacidad):
// el veterinario inactivo no puede iniciar sesión ni dar tratamientos, pero no se borra
public record EstadoVeterinarioRequest(@NotNull(message = "Indica si el veterinario queda activo") Boolean activo) {
}
