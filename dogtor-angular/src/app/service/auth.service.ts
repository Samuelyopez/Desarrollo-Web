import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { API_URL } from '../api.config';
import { LoginRequest, Rol, UsuarioLogueado } from '../models/usuario.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);

  // Sin sesión ni token: el usuario que inició sesión vive solo en memoria.
  // Al recargar la página (F5) se pierde y hay que volver a ingresar
  private usuarioActual = signal<UsuarioLogueado | null>(null);

  usuario = this.usuarioActual.asReadonly();
  rol = computed(() => this.usuarioActual()?.rol ?? null);
  estaLogueado = computed(() => this.usuarioActual() !== null);

  // Página de inicio de cada portal
  private readonly inicioPorRol: Record<Rol, string> = {
    DUENO: '/cliente',
    VETERINARIO: '/vet',
    ADMIN: '/admin',
  };

  // Valida correo y contraseña en la API. Si responde 200 guarda el usuario;
  // los errores (401, 403, 400) llegan al componente para mostrar el mensaje
  login(credenciales: LoginRequest): Observable<UsuarioLogueado> {
    return this.http
      .post<UsuarioLogueado>(`${API_URL}/auth/login`, credenciales)
      .pipe(tap((usuario) => this.usuarioActual.set(usuario)));
  }

  logout() {
    this.usuarioActual.set(null);
  }

  rutaInicio(rol: Rol) {
    return this.inicioPorRol[rol];
  }
}
