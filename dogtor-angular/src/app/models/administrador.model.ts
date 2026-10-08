// Entidad Administrador (tabla administradores)
export interface Administrador {
  id: number;
  cedula: string;
  nombre: string;
  correo?: string; // viene del Usuario asociado
}
