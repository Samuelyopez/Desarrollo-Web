// Respuesta de GET /api/dashboard (DashboardResponse en Spring Boot)

export interface MedicamentoCantidad {
  medicamento: string;
  tratamientos: number;
  unidades: number;
}

export interface TopMedicamento {
  medicamento: string;
  unidades: number;
  ventas: number;
}

export interface Dashboard {
  fechaDesde: string; // LocalDate ISO: inicio de los últimos 30 días
  fechaHasta: string;
  tratamientosUltimoMes: number;
  unidadesUltimoMes: number;
  tratamientosPorMedicamento: MedicamentoCantidad[];
  veterinariosActivos: number;
  veterinariosInactivos: number;
  mascotasTotales: number;
  mascotasActivas: number;
  ventasTotales: number;
  gananciasTotales: number;
  topMedicamentos: TopMedicamento[];
}
