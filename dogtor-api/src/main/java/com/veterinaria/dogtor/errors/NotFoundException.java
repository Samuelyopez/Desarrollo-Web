package com.veterinaria.dogtor.errors;

// El recurso pedido no existe (404)
public class NotFoundException extends RuntimeException {

    public NotFoundException(String mensaje) {
        super(mensaje);
    }
}
