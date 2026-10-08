import { HttpErrorResponse } from '@angular/common/http';
import { ErrorResponse } from '../models/error-response.model';

// Texto para mostrar cuando falla una petición a la API.
// status 0 = la API no responde; en los demás casos se usa el mensaje que envía Spring Boot
export function mensajeError(error: HttpErrorResponse, porDefecto = 'Ocurrió un error inesperado.') {
  if (error.status === 0) {
    return 'No se pudo conectar con el servidor. ¿Está encendida la API?';
  }
  const cuerpo = error.error as ErrorResponse | null;
  return cuerpo?.mensaje ?? porDefecto;
}
