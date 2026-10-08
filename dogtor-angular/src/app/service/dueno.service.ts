import { Injectable } from '@angular/core';
import { Dueno } from '../models/dueno.model';

@Injectable({
  providedIn: 'root',
})
export class DuenoService {
  // "Base de datos" quemada con los mismos dueños del DataLoader de Spring Boot.
  // En el Sprint 2 se reemplaza por peticiones HTTP a la API
  private duenoArray: Dueno[] = [
    { id: 1, cedula: '1000000001', nombre: 'Juan Pérez', celular: '3001234567', correo: 'juan.perez@correo.com' },
    { id: 2, cedula: '1000000002', nombre: 'María López', celular: '3012345678', correo: 'maria.lopez@correo.com' },
    { id: 3, cedula: '1000000003', nombre: 'Carlos Ruiz', celular: '3023456789', correo: 'carlos.ruiz@correo.com' },
    { id: 4, cedula: '1000000004', nombre: 'Ana Gómez', celular: '3034567890', correo: 'ana.gomez@correo.com' },
    { id: 5, cedula: '1000000005', nombre: 'Luis Díaz', celular: '3045678901', correo: 'luis.diaz@correo.com' },
    { id: 6, cedula: '1000000006', nombre: 'Sofía Martínez', celular: '3056789012', correo: 'sofia.martinez@correo.com' },
    { id: 7, cedula: '1000000007', nombre: 'Andrés Rodríguez', celular: '3067890123', correo: 'andres.rodriguez@correo.com' },
    { id: 8, cedula: '1000000008', nombre: 'Camila Torres', celular: '3078901234', correo: 'camila.torres@correo.com' },
    { id: 9, cedula: '1000000009', nombre: 'Diego Ramírez', celular: '3089012345', correo: 'diego.ramirez@correo.com' },
    { id: 10, cedula: '1000000010', nombre: 'Valentina Jiménez', celular: '3090123456', correo: 'valentina.jimenez@correo.com' },
  ];

  getDuenos() {
    return this.duenoArray;
  }

  getDuenoById(id: number) {
    return this.duenoArray.find((d) => d.id === id);
  }

  // Busca por nombre, cédula, celular o correo (sin distinguir mayúsculas)
  buscarDuenos(texto: string) {
    const filtro = texto.trim().toLowerCase();
    if (!filtro) {
      return this.duenoArray;
    }
    return this.duenoArray.filter((d) =>
      [d.nombre, d.cedula, d.celular ?? '', d.correo ?? ''].some((campo) => campo.toLowerCase().includes(filtro)),
    );
  }

  addDueno(dueno: Dueno) {
    // Siguiente id = máximo actual + 1 (no se repite aunque se eliminen dueños)
    dueno.id = Math.max(0, ...this.duenoArray.map((d) => d.id)) + 1;
    this.duenoArray.push(dueno);
  }

  updateDueno(id: number, cambios: Dueno) {
    const dueno = this.getDuenoById(id);
    if (dueno) {
      // Se modifica el MISMO objeto (no se reemplaza): las mascotas guardan la referencia
      // al objeto Dueno, así ven los datos nuevos sin tener que actualizarlas una a una
      dueno.cedula = cambios.cedula;
      dueno.nombre = cambios.nombre;
      dueno.celular = cambios.celular;
      dueno.correo = cambios.correo;
    }
  }

  // Ojo: quien llama debe comprobar antes que el dueño no tenga mascotas
  // (en el Sprint 2 la API las borra en cascada)
  deleteDueno(dueno: Dueno) {
    this.duenoArray = this.duenoArray.filter((d) => d.id !== dueno.id);
  }
}
