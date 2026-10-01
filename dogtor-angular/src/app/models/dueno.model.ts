// Entidad Dueno (tabla duenos): el cliente dueño de las mascotas
export interface Dueno {
  id: number;
  nombre: string;
  telefono?: string;
  direccion?: string;
  fechaCreacion?: string; // formato ISO, ej. "2026-09-28T10:00:00"
  fechaActualizacion?: string;
  usuarioId?: number; // @OneToOne con Usuario
}
