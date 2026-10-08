// Fila de "Mis pacientes" (PacienteResponse en Spring Boot): mascota tratada por el veterinario
export interface Paciente {
  mascotaId: number;
  nombre: string;
  raza?: string;
  foto?: string;
  activa: boolean;
  duenoNombre: string;
  duenoCedula: string;
  cantidadTratamientos: number;
  ultimoTratamiento: string; // LocalDate en formato ISO
}
