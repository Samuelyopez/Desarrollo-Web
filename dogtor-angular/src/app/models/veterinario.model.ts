// Entidad Veterinario (tabla veterinarios)
export interface Veterinario {
  id: number;
  cedula: string;
  nombre: string;
  especialidad?: string;
  foto?: string; // URL
  correo?: string; // viene del Usuario asociado
  activo: boolean; // estado laboral (vacaciones/incapacidad = inactivo)
}
