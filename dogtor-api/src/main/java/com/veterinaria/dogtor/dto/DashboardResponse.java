package com.veterinaria.dogtor.dto;

import java.time.LocalDate;
import java.util.List;

// Indicadores del dashboard del administrador (AC29).
// Ventas, ganancias y unidades se calculan sobre los tratamientos registrados (con los precios copiados en
// cada tratamiento); no incluyen las unidades vendidas históricas que trae el Excel de medicamentos
public record DashboardResponse(
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        long tratamientosUltimoMes,
        long unidadesUltimoMes,
        List<MedicamentoCantidad> tratamientosPorMedicamento,
        long veterinariosActivos,
        long veterinariosInactivos,
        long mascotasTotales,
        long mascotasActivas,
        double ventasTotales,
        double gananciasTotales,
        List<TopMedicamento> topMedicamentos) {
}
