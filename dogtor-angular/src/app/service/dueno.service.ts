import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../api.config';
import { Dueno, DuenoRequest } from '../models/dueno.model';
import { Mascota } from '../models/mascota.model';

@Injectable({
  providedIn: 'root',
})
export class DuenoService {
  private http = inject(HttpClient);
  private url = `${API_URL}/duenos`;

  // Todos los dueños, o solo los que coinciden con el texto (nombre, cédula, celular o correo)
  getDuenos(buscar = ''): Observable<Dueno[]> {
    const texto = buscar.trim();
    const params = texto ? new HttpParams().set('buscar', texto) : undefined;
    return this.http.get<Dueno[]>(this.url, { params });
  }

  getDuenoByCedula(cedula: string): Observable<Dueno> {
    return this.http.get<Dueno>(`${this.url}/${cedula}`);
  }

  getMascotasByDueno(cedula: string): Observable<Mascota[]> {
    return this.http.get<Mascota[]>(`${this.url}/${cedula}/mascotas`);
  }

  // Portal cliente: la API responde 404 si la mascota no es de ese dueño
  getMascotaDeDueno(cedula: string, id: number): Observable<Mascota> {
    return this.http.get<Mascota>(`${this.url}/${cedula}/mascotas/${id}`);
  }

  createDueno(dueno: DuenoRequest): Observable<Dueno> {
    return this.http.post<Dueno>(this.url, dueno);
  }

  updateDueno(cedula: string, dueno: DuenoRequest): Observable<Dueno> {
    return this.http.put<Dueno>(`${this.url}/${cedula}`, dueno);
  }

  // La API borra también sus mascotas y su usuario; los tratamientos se conservan
  deleteDueno(cedula: string): Observable<void> {
    return this.http.delete<void>(`${this.url}/${cedula}`);
  }
}
