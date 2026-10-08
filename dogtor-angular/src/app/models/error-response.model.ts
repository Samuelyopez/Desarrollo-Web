// Formato de los errores de la API (ErrorResponse en Spring Boot)
export interface ErrorResponse {
  status: number;
  error: string;
  mensaje: string;
  timestamp: string;
}
