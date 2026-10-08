package com.veterinaria.dogtor.errors;

// El usuario existe pero está desactivado (403)
public class UsuarioInactivoException extends RuntimeException {

    public UsuarioInactivoException(String mensaje) {
        super(mensaje);
    }
}
