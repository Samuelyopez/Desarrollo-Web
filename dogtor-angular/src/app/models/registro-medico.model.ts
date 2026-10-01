// Entidad RegistroMedico (tabla registros_medicos): historial clínico de una mascota
export interface RegistroMedico {
  id: number;
  diagnostico: string;
  tratamiento?: string;
  fecha: string; // formato ISO
  mascotaId: number; // @ManyToOne con Mascota
  veterinarioId?: number; // @ManyToOne con Usuario (rol VETERINARIO)
  drogaIds: number[]; // @ManyToMany con Droga
}
