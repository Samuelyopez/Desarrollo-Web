package com.veterinaria.dogtor.errors;

// El recurso choca con otro ya existente, ej. cédula o correo repetidos (409)
public class ConflictException extends RuntimeException {

    public ConflictException(String mensaje) {
        super(mensaje);
    }
}
