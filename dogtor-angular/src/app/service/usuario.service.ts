import { Injectable } from '@angular/core';
import { Usuario } from '../models/usuario.model';

@Injectable({
  providedIn: 'root',
})
export class UsuarioService {
  // "Base de datos" quemada: usuarios de prueba del DataInitializer de Spring Boot
  private usuarioArray: Usuario[] = [
    { id: 1, nombre: 'Administrador DogTor', correo: 'administrador@dogtor.com', password: 'admin123', rol: 'ADMIN', activo: true },
    { id: 2, nombre: 'Dr. Andres Felipe', correo: 'veterinario@dogtor.com', password: 'vet123', rol: 'VETERINARIO', activo: true },
    { id: 3, nombre: 'Dra. Laura Jimenez', correo: 'laura.vet@dogtor.com', password: 'vet123', rol: 'VETERINARIO', activo: true },
  ];

  getUsuarios() {
    return this.usuarioArray;
  }

  getUsuarioById(id: number) {
    return this.usuarioArray.find((u) => u.id === id);
  }
}
