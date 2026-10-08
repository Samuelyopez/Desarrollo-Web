// Equivalente al enum Rol de Spring Boot
export type Rol = 'DUENO' | 'VETERINARIO' | 'ADMIN';

// Cuerpo de POST /api/auth/login
export interface LoginRequest {
  correo: string;
  password: string;
}

// Respuesta del login (LoginResponse en Spring Boot).
// perfilId y cedula son los del Dueno, Veterinario o Administrador según el rol
export interface UsuarioLogueado {
  id: number;
  nombre: string;
  correo: string;
  rol: Rol;
  perfilId: number;
  cedula: string;
}
