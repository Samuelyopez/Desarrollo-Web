package com.veterinaria.dogtor.errors;

// Datos inválidos que no cubre Bean Validation, ej. contraseña faltante al crear (400)
public class BadRequestException extends RuntimeException {

    public BadRequestException(String mensaje) {
        super(mensaje);
    }
}
