// Entidad Veterinario (tabla veterinarios)
export interface Veterinario {
  id: number;
  cedula: string;
  nombre: string;
  especialidad?: string;
  foto?: string; // URL
  correo?: string; // viene del Usuario asociado
  activo: boolean; // estado laboral (vacaciones/incapacidad = inactivo)
  cantidadAtenciones?: number; // tratamientos que ha dado (lo calcula la API)
}

// Cuerpo de POST y PUT /api/veterinarios (VeterinarioRequest en Spring Boot).
// password es obligatoria al crear; al editar, si no se envía se conserva la anterior
export interface VeterinarioRequest {
  cedula: string;
  nombre: string;
  especialidad: string;
  foto?: string;
  correo: string;
  password?: string;
}
