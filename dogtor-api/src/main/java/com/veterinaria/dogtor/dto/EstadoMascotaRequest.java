package com.veterinaria.dogtor.dto;

import jakarta.validation.constraints.NotNull;

// Cuerpo de PUT /api/mascotas/{id}/estado: activa = en la clínica, inactiva = en casa
public record EstadoMascotaRequest(@NotNull(message = "Indica si la mascota queda activa") Boolean activa) {
}
