package com.veterinaria.dogtor.dto;

import java.time.LocalDateTime;

// Formato común de todos los errores de la API
public record ErrorResponse(int status, String error, String mensaje, LocalDateTime timestamp) {
}
