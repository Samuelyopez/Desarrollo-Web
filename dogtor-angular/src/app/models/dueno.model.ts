import { Mascota } from './mascota.model';

// Entidad Dueno (tabla duenos): el cliente dueño de las mascotas.
// Los nombres de los campos son iguales a los de Spring Boot
export interface Dueno {
  id: number;
  cedula: string;
  nombre: string;
  celular?: string;
  correo?: string; // viene del Usuario asociado
  mascotas?: Mascota[]; // relación: la API no la envía, se pide aparte
}
