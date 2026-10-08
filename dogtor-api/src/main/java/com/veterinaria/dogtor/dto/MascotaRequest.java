package com.veterinaria.dogtor.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Cuerpo de POST y PUT /api/mascotas. El estado (activa) se cambia aparte en PUT /{id}/estado
public record MascotaRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
        @Pattern(regexp = "[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]+", message = "El nombre solo puede contener letras")
        String nombre,

        @Size(max = 50, message = "La raza no puede superar 50 caracteres")
        String raza,

        @Min(value = 0, message = "La edad no puede ser negativa")
        @Max(value = 99, message = "La edad no puede superar 99 años")
        Integer edad,

        @DecimalMin(value = "0.1", message = "El peso debe ser mayor a 0")
        @DecimalMax(value = "200.0", message = "El peso no puede superar 200 kg")
        Double peso,

        // Puede quedar vacía mientras un veterinario la atiende
        @Size(max = 100, message = "La enfermedad no puede superar 100 caracteres")
        String enfermedad,

        @Size(max = 500, message = "La URL de la foto es demasiado larga")
        @Pattern(regexp = "^$|^(https?://|/).+", message = "La foto debe ser una URL http(s):// o una ruta local /img/...")
        String foto,

        @NotBlank(message = "El dueño es obligatorio")
        String duenoCedula) {
}
