package com.veterinaria.dogtor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Cuerpo de POST /api/auth/login
public record LoginRequest(
        @NotBlank(message = "El correo es obligatorio") @Email(message = "El correo no es válido") String correo,
        @NotBlank(message = "La contraseña es obligatoria") String password) {
}
