import { Dueno } from './dueno.model';

// Entidad Mascota (tabla mascotas)
export interface Mascota {
  id: number;
  nombre: string;
  raza?: string;
  edad?: string;
  fotoUrl?: string;
  vacunas?: string;
  activa: boolean;
  dueno?: Dueno; // @ManyToOne con Dueno: se guarda el objeto, no el id
}
