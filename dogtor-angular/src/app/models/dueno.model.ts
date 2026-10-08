// Entidad Dueno (tabla duenos): el cliente dueño de las mascotas.
// Los nombres de los campos son iguales a los de Spring Boot
export interface Dueno {
  id: number;
  cedula: string;
  nombre: string;
  celular?: string;
  correo?: string; // viene del Usuario asociado
  cantidadMascotas?: number; // lo calcula la API; sus mascotas se piden aparte
}

// Cuerpo de POST y PUT /api/duenos (DuenoRequest en Spring Boot).
// password es obligatoria al crear; al editar, si no se envía se conserva la anterior
export interface DuenoRequest {
  cedula: string;
  nombre: string;
  celular: string;
  correo: string;
  password?: string;
}
