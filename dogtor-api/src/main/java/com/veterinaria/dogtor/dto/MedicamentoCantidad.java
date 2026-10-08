package com.veterinaria.dogtor.dto;

// Fila del KPI "tratamientos por medicamento": cuántos tratamientos y cuántas unidades en el periodo
public record MedicamentoCantidad(String medicamento, Long tratamientos, Long unidades) {
}
