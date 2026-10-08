package com.veterinaria.dogtor.dto;

import com.veterinaria.dogtor.entities.Rol;

// Usuario que inició sesión. perfilId y cedula son los del Dueno, Veterinario o Administrador según el rol
public record LoginResponse(Long id, String nombre, String correo, Rol rol, Long perfilId, String cedula) {
}
