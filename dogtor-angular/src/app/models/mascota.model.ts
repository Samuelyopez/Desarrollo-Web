// Entidad Mascota (tabla mascotas)
export interface Mascota {
  id: number;
  nombre: string;
  raza?: string;
  edad?: string;
  fotoUrl?: string;
  vacunas?: string;
  activa: boolean;
  duenoId?: number; // @ManyToOne con Dueno
}
