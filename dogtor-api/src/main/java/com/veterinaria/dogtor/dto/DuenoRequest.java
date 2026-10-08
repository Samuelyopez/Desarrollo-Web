package com.veterinaria.dogtor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Cuerpo de POST y PUT /api/duenos. La contraseña es obligatoria al crear y opcional al editar
// (vacía = no cambiarla); eso lo valida el servicio
public record DuenoRequest(
        @NotBlank(message = "La cédula es obligatoria")
        @Pattern(regexp = "\\d{6,10}", message = "La cédula debe tener entre 6 y 10 dígitos")
        String cedula,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 80, message = "El nombre debe tener entre 3 y 80 caracteres")
        String nombre,

        @NotBlank(message = "El celular es obligatorio")
        @Pattern(regexp = "3\\d{9}", message = "El celular debe tener 10 dígitos y empezar por 3")
        String celular,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no es válido")
        String correo,

        @Size(min = 6, max = 50, message = "La contraseña debe tener entre 6 y 50 caracteres")
        String password) {
}
