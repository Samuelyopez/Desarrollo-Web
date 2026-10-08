import { Dueno } from './dueno.model';

// Entidad Mascota (tabla mascotas)
export interface Mascota {
  id: number;
  nombre: string;
  raza?: string;
  edad?: number; // en años
  peso?: number; // en kg
  enfermedad?: string;
  foto?: string; // URL
  activa: boolean;
  duenoId?: number; // la API envía solo el id del dueño
  dueno?: Dueno; // relación: se arma en el front cuando hace falta
}
