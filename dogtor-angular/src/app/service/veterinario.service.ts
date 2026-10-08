import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../api.config';
import { Paciente } from '../models/paciente.model';

@Injectable({
  providedIn: 'root',
})
export class VeterinarioService {
  private http = inject(HttpClient);
  private url = `${API_URL}/veterinarios`;

  // Mascotas que ha tratado el veterinario (id = perfilId del usuario logueado)
  getPacientes(veterinarioId: number): Observable<Paciente[]> {
    return this.http.get<Paciente[]>(`${this.url}/${veterinarioId}/pacientes`);
  }
}
