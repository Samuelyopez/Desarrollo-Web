package com.veterinaria.dogtor.dto;

// Fila del top de medicamentos con más unidades vendidas en tratamientos
public record TopMedicamento(String medicamento, Long unidades, Double ventas) {
}
