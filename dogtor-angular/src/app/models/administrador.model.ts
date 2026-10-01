// Entidad Administrador (tabla administradores)
// La relación @OneToOne con Usuario se guarda como el id del usuario
export interface Administrador {
  id: number;
  cargo?: string;
  usuarioId: number;
}
