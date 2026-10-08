// Entidad Tratamiento (tabla tratamientos): medicamento que un veterinario da a una mascota.
// La API no envía las relaciones: medicamentoNombre y veterinarioNombre salen de getters de Spring Boot
export interface Tratamiento {
  id: number;
  fecha: string; // LocalDate en formato ISO, ej. "2026-10-08"
  cantidad: number;
  precioVenta: number; // precio unitario copiado al crear el tratamiento
  mascotaNombre: string; // se conserva aunque se borre la mascota
  medicamentoNombre: string;
  veterinarioNombre: string;
  veterinarioEspecialidad?: string;
}

// Cuerpo de POST /api/tratamientos (TratamientoRequest en Spring Boot). La fecha la pone la API (hoy)
export interface TratamientoRequest {
  mascotaId: number;
  veterinarioId: number;
  medicamentoId: number;
  cantidad: number;
}
