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
  duenoId?: number; // TEMPORAL: convive con "dueno" hasta que el formulario use el objeto; luego se elimina
  dueno?: Dueno; // @ManyToOne con Dueno: se guarda el objeto, no el id
}
