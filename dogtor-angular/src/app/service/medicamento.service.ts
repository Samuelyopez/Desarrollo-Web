import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../api.config';
import { Medicamento } from '../models/medicamento.model';

@Injectable({
  providedIn: 'root',
})
export class MedicamentoService {
  private http = inject(HttpClient);
  private url = `${API_URL}/medicamentos`;

  // soloDisponibles = solo los que tienen unidades (desplegable de tratamientos)
  getMedicamentos(soloDisponibles = false): Observable<Medicamento[]> {
    const params = new HttpParams().set('disponibles', soloDisponibles);
    return this.http.get<Medicamento[]>(this.url, { params });
  }
}
