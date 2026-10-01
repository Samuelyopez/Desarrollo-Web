// Entidad Veterinario (tabla veterinarios)
// La relación @OneToOne con Usuario se guarda como el id del usuario
export interface Veterinario {
  id: number;
  especialidad?: string;
  numeroLicencia?: string;
  usuarioId: number;
}
