package com.veterinaria.dogtor.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// Cuerpo de POST /api/tratamientos. La fecha la pone el servidor (hoy)
public record TratamientoRequest(
        @NotNull(message = "La mascota es obligatoria") Long mascotaId,
        @NotNull(message = "El veterinario es obligatorio") Long veterinarioId,
        @NotNull(message = "El medicamento es obligatorio") Long medicamentoId,
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        @Max(value = 100, message = "La cantidad no puede superar 100")
        Integer cantidad) {
}
