import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../api.config';
import { Tratamiento, TratamientoRequest } from '../models/tratamiento.model';

// El historial de una mascota se pide con MascotaService.getTratamientos
@Injectable({
  providedIn: 'root',
})
export class TratamientoService {
  private http = inject(HttpClient);
  private url = `${API_URL}/tratamientos`;

  // La API valida que la mascota esté activa y que haya unidades (409 si no)
  createTratamiento(tratamiento: TratamientoRequest): Observable<Tratamiento> {
    return this.http.post<Tratamiento>(this.url, tratamiento);
  }
}
