import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../api.config';
import { Mascota, MascotaRequest } from '../models/mascota.model';

// Las mascotas no se eliminan: solo se activan (en la clínica) o desactivan (en casa)
@Injectable({
  providedIn: 'root',
})
export class MascotaService {
  private http = inject(HttpClient);
  private url = `${API_URL}/mascotas`;

  // Todas las mascotas, o solo las que coinciden con el texto (nombre, raza o dueño)
  getMascotas(buscar = ''): Observable<Mascota[]> {
    const texto = buscar.trim();
    const params = texto ? new HttpParams().set('buscar', texto) : undefined;
    return this.http.get<Mascota[]>(this.url, { params });
  }

  getMascotaById(id: number): Observable<Mascota> {
    return this.http.get<Mascota>(`${this.url}/${id}`);
  }

  createMascota(mascota: MascotaRequest): Observable<Mascota> {
    return this.http.post<Mascota>(this.url, mascota);
  }

  updateMascota(id: number, mascota: MascotaRequest): Observable<Mascota> {
    return this.http.put<Mascota>(`${this.url}/${id}`, mascota);
  }

  cambiarEstado(id: number, activa: boolean): Observable<Mascota> {
    return this.http.put<Mascota>(`${this.url}/${id}/estado`, { activa });
  }
}
