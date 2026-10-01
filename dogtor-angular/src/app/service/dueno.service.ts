import { Injectable } from '@angular/core';
import { Dueno } from '../models/dueno.model';

@Injectable({
  providedIn: 'root',
})
export class DuenoService {
  // "Base de datos" quemada con los mismos dueños del DataInitializer de Spring Boot
  private duenoArray: Dueno[] = [
    { id: 1, nombre: 'Juan Pérez', telefono: '3001234567', direccion: 'Calle 10 # 5-20', fechaCreacion: '2026-08-01T09:00:00' },
    { id: 2, nombre: 'María López', telefono: '3012345678', direccion: 'Carrera 7 # 45-10', fechaCreacion: '2026-08-02T10:30:00' },
    { id: 3, nombre: 'Carlos Ruiz', telefono: '3023456789', direccion: 'Avenida 68 # 12-03', fechaCreacion: '2026-08-03T11:15:00' },
    { id: 4, nombre: 'Ana Gómez', telefono: '3034567890', direccion: 'Calle 80 # 20-45', fechaCreacion: '2026-08-04T14:00:00' },
    { id: 5, nombre: 'Luis Díaz', telefono: '3045678901', direccion: 'Carrera 15 # 93-60', fechaCreacion: '2026-08-05T08:45:00' },
    { id: 6, nombre: 'Sofía Martínez', telefono: '3056789012', direccion: 'Calle 53 # 24-11', fechaCreacion: '2026-08-06T16:20:00' },
    { id: 7, nombre: 'Andrés Rodríguez', telefono: '3067890123', direccion: 'Carrera 30 # 1-50', fechaCreacion: '2026-08-07T12:10:00' },
    { id: 8, nombre: 'Camila Torres', telefono: '3078901234', direccion: 'Calle 26 # 69-76', fechaCreacion: '2026-08-08T09:30:00' },
    { id: 9, nombre: 'Diego Ramírez', telefono: '3089012345', direccion: 'Avenida Boyacá # 64-20', fechaCreacion: '2026-08-09T15:40:00' },
    { id: 10, nombre: 'Valentina Jiménez', telefono: '3090123456', direccion: 'Calle 127 # 7-19', fechaCreacion: '2026-08-10T10:00:00' },
  ];

  getDuenos() {
    return this.duenoArray;
  }

  getDuenoById(id: number) {
    return this.duenoArray.find((d) => d.id === id);
  }
}
