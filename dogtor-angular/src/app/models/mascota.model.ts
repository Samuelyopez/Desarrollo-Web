// Entidad Mascota (tabla mascotas). Los nombres de los campos son iguales a los de Spring Boot
export interface Mascota {
  id: number;
  nombre: string;
  raza?: string;
  edad?: number; // en años
  peso?: number; // en kg
  enfermedad?: string; // puede estar vacía mientras se diagnostica
  foto?: string; // URL
  activa: boolean; // true = en la clínica, false = en casa
  // Datos básicos del dueño (la API no envía el objeto Dueno; si hace falta completo se pide aparte)
  duenoId?: number;
  duenoCedula?: string;
  duenoNombre?: string;
}

// Cuerpo de POST y PUT /api/mascotas (MascotaRequest en Spring Boot).
// El estado se cambia aparte con PUT /api/mascotas/{id}/estado
export interface MascotaRequest {
  nombre: string;
  raza?: string;
  edad?: number;
  peso?: number;
  enfermedad?: string;
  foto?: string;
  duenoCedula: string;
}
