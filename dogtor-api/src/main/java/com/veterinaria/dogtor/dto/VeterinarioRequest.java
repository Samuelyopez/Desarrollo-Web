package com.veterinaria.dogtor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Cuerpo de POST y PUT /api/veterinarios. La contraseña es obligatoria al crear y opcional al editar
// (vacía = no cambiarla); eso lo valida el servicio. El estado se cambia aparte en PUT /{cedula}/estado
public record VeterinarioRequest(
        @NotBlank(message = "La cédula es obligatoria")
        @Pattern(regexp = "\\d{6,10}", message = "La cédula debe tener entre 6 y 10 dígitos")
        String cedula,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 80, message = "El nombre debe tener entre 3 y 80 caracteres")
        @Pattern(regexp = "[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]+", message = "El nombre solo puede contener letras")
        String nombre,

        @NotBlank(message = "La especialidad es obligatoria")
        @Size(max = 60, message = "La especialidad no puede superar 60 caracteres")
        String especialidad,

        @Size(max = 500, message = "La URL de la foto es demasiado larga")
        @Pattern(regexp = "^$|^(https?://|/).+", message = "La foto debe ser una URL http(s):// o una ruta local /img/...")
        String foto,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no es válido")
        String correo,

        @Size(min = 6, max = 50, message = "La contraseña debe tener entre 6 y 50 caracteres")
        String password) {
}
