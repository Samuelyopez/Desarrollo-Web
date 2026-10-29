import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../api.config';
import { LoginRequest, UsuarioLogueado } from '../models/usuario.model';

// Sin sesión: no se guarda ningún usuario. El login solo valida las credenciales en la API
// y dice a qué portal ir; la cédula viaja en la URL del portal
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);

  // Valida correo y contraseña en la API. Los errores (401, 403, 400) llegan al componente
  login(credenciales: LoginRequest): Observable<UsuarioLogueado> {
    return this.http.post<UsuarioLogueado>(`${API_URL}/auth/login`, credenciales);
  }

  // Página de inicio del portal de cada rol, con la cédula en la ruta
  rutaInicio(usuario: UsuarioLogueado) {
    switch (usuario.rol) {
      case 'DUENO':
        return `/cliente/${usuario.cedula}`;
      case 'VETERINARIO':
        return `/vet/${usuario.cedula}`;
      default:
        return '/admin';
    }
  }
}
