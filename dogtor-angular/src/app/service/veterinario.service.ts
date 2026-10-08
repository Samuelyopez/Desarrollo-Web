import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../api.config';
import { Paciente } from '../models/paciente.model';
import { Veterinario, VeterinarioRequest } from '../models/veterinario.model';

// Los veterinarios se identifican por cédula, salvo en "Mis pacientes" (id = perfilId del logueado)
@Injectable({
  providedIn: 'root',
})
export class VeterinarioService {
  private http = inject(HttpClient);
  private url = `${API_URL}/veterinarios`;

  // Todos los veterinarios, o solo los que coinciden con el texto (nombre, cédula, especialidad o correo)
  getVeterinarios(buscar = ''): Observable<Veterinario[]> {
    const texto = buscar.trim();
    const params = texto ? new HttpParams().set('buscar', texto) : undefined;
    return this.http.get<Veterinario[]>(this.url, { params });
  }

  getVeterinarioByCedula(cedula: string): Observable<Veterinario> {
    return this.http.get<Veterinario>(`${this.url}/${cedula}`);
  }

  createVeterinario(veterinario: VeterinarioRequest): Observable<Veterinario> {
    return this.http.post<Veterinario>(this.url, veterinario);
  }

  updateVeterinario(cedula: string, veterinario: VeterinarioRequest): Observable<Veterinario> {
    return this.http.put<Veterinario>(`${this.url}/${cedula}`, veterinario);
  }

  // Estado laboral: el inactivo no puede iniciar sesión ni dar tratamientos
  cambiarEstado(cedula: string, activo: boolean): Observable<Veterinario> {
    return this.http.put<Veterinario>(`${this.url}/${cedula}/estado`, { activo });
  }

  // Mascotas que ha tratado el veterinario (id = perfilId del usuario logueado)
  getPacientes(veterinarioId: number): Observable<Paciente[]> {
    return this.http.get<Paciente[]>(`${this.url}/${veterinarioId}/pacientes`);
  }
}
