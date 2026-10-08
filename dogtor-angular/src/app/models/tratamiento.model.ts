import { Mascota } from './mascota.model';
import { Veterinario } from './veterinario.model';
import { Medicamento } from './medicamento.model';

// Entidad Tratamiento (tabla tratamientos): medicamento que un veterinario da a una mascota
export interface Tratamiento {
  id: number;
  fecha: string; // LocalDate en formato ISO, ej. "2026-10-08"
  cantidad: number;
  precioVenta: number; // copia tomada al crear el tratamiento
  precioCompra: number;
  mascotaNombre: string; // se conserva aunque se borre la mascota
  // Relaciones: la API no las envía, se piden aparte
  mascota?: Mascota;
  veterinario?: Veterinario;
  medicamento?: Medicamento;
}
