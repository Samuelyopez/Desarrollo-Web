package com.veterinaria.dogtor.errors;

// Correo o contraseña incorrectos (401)
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
