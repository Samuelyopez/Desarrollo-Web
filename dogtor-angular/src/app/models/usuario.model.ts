// Equivalente al enum RolUsuario de Spring Boot
export type RolUsuario = 'ADMIN' | 'VETERINARIO' | 'DUENO';

// Entidad Usuario (tabla usuarios): credenciales y rol de acceso
export interface Usuario {
  id: number;
  nombre?: string;
  correo: string;
  password: string;
  rol: RolUsuario;
  activo: boolean;
}
