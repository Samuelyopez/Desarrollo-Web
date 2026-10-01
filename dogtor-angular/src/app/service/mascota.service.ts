import { Injectable, inject } from '@angular/core';
import { Mascota } from '../models/mascota.model';
import { Dueno } from '../models/dueno.model';
import { RegistroMedicoService } from './registro-medico.service';
import { DuenoService } from './dueno.service';

@Injectable({
  providedIn: 'root',
})
export class MascotaService {
  //DI
  private registroMedicoService = inject(RegistroMedicoService);
  // Debe ir ANTES de mascotaArray: los inicializadores de campos se ejecutan en orden
  // y el arreglo de abajo ya usa duenoService para guardar el objeto Dueno
  private duenoService = inject(DuenoService);

  // "Base de datos" quemada: el servicio es un singleton, así que los cambios
  // se mantienen mientras navegas, pero al recargar (F5) vuelven estos datos.
  private mascotaArray: Mascota[] = [
    { id: 1, nombre: 'Max', raza: 'Golden Retriever', edad: '2 meses', fotoUrl: '/img/mascotas/1-max.jpg', vacunas: 'Al día', activa: true, dueno: this.duenoService.getDuenoById(1) },
    { id: 2, nombre: 'Luna', raza: 'Gato Siamés', edad: '1 año', fotoUrl: '/img/mascotas/2-luna.jpg', vacunas: 'Falta vacuna de rabia', activa: true, dueno: this.duenoService.getDuenoById(1) },
    { id: 3, nombre: 'Rocky', raza: 'Bulldog Francés', edad: '8 meses', fotoUrl: '/img/mascotas/3-rocky.jpg', vacunas: 'Falta refuerzo anual', activa: true, dueno: this.duenoService.getDuenoById(2) },
    { id: 4, nombre: 'Bella', raza: 'Gato Persa', edad: '3 años', fotoUrl: '/img/mascotas/4-bella.jpg', vacunas: 'Desparasitado', activa: true, dueno: this.duenoService.getDuenoById(2) },
    { id: 5, nombre: 'Toby', raza: 'Poodle', edad: '1 año y medio', fotoUrl: '/img/mascotas/5-toby.jpg', vacunas: 'Falta vacuna de rabia', activa: true, dueno: this.duenoService.getDuenoById(3) },
    { id: 6, nombre: 'Oreo', raza: 'Gato Naranja Común', edad: '7 años', fotoUrl: '/img/mascotas/6-oreo.jpg', vacunas: 'Esquema completo', activa: true, dueno: this.duenoService.getDuenoById(3) },
    { id: 7, nombre: 'Simba', raza: 'Golden Retriever', edad: '4 años', fotoUrl: '/img/mascotas/7-simba.jpg', vacunas: 'Desparasitado', activa: true, dueno: this.duenoService.getDuenoById(4) },
    { id: 8, nombre: 'Felix', raza: 'Gato Común Europeo', edad: '6 meses', fotoUrl: '/img/mascotas/8-felix.jpg', vacunas: 'Vacuna múltiple pendiente', activa: false, dueno: this.duenoService.getDuenoById(4) },
    { id: 9, nombre: 'Milo', raza: 'Labrador Retriever', edad: '2 meses', fotoUrl: '/img/mascotas/9-milo.jpg', vacunas: 'Esquema completo', activa: true, dueno: this.duenoService.getDuenoById(5) },
    { id: 10, nombre: 'Pelusa', raza: 'Gato Negro', edad: '1 año', fotoUrl: '/img/mascotas/10-pelusa.jpg', vacunas: 'Al día', activa: true, dueno: this.duenoService.getDuenoById(5) },
    { id: 11, nombre: 'Thor', raza: 'Husky Siberiano', edad: '8 meses', fotoUrl: '/img/mascotas/11-thor.jpg', vacunas: 'Vacuna múltiple pendiente', activa: true, dueno: this.duenoService.getDuenoById(6) },
    { id: 12, nombre: 'Michi', raza: 'Gato Atigrado', edad: '3 años', fotoUrl: '/img/mascotas/12-michi.jpg', vacunas: 'Falta refuerzo anual', activa: true, dueno: this.duenoService.getDuenoById(6) },
    { id: 13, nombre: 'Buddy', raza: 'Pastor Alemán', edad: '1 año y medio', fotoUrl: '/img/mascotas/13-buddy.jpg', vacunas: 'Al día', activa: true, dueno: this.duenoService.getDuenoById(7) },
    { id: 14, nombre: 'Salem', raza: 'Gato Naranja', edad: '7 años', fotoUrl: '/img/mascotas/14-salem.jpg', vacunas: 'Falta vacuna de rabia', activa: true, dueno: this.duenoService.getDuenoById(7) },
    { id: 15, nombre: 'Duke', raza: 'Mestizo', edad: '4 años', fotoUrl: '/img/mascotas/15-duke.jpg', vacunas: 'Falta refuerzo anual', activa: true, dueno: this.duenoService.getDuenoById(8) },
    { id: 16, nombre: 'Whiskers', raza: 'Ragdoll', edad: '6 meses', fotoUrl: '/img/mascotas/16-whiskers.jpg', vacunas: 'Desparasitado', activa: true, dueno: this.duenoService.getDuenoById(8) },
    { id: 17, nombre: 'Rex', raza: 'Chihuahua', edad: '2 meses', fotoUrl: '/img/mascotas/17-rex.jpg', vacunas: 'Falta vacuna de rabia', activa: true, dueno: this.duenoService.getDuenoById(9) },
    { id: 18, nombre: 'Nala', raza: 'Gato Mestizo', edad: '1 año', fotoUrl: '/img/mascotas/18-nala.jpg', vacunas: 'Esquema completo', activa: true, dueno: this.duenoService.getDuenoById(9) },
    { id: 19, nombre: 'Zeus', raza: 'Pug', edad: '8 meses', fotoUrl: '/img/mascotas/19-zeus.jpg', vacunas: 'Desparasitado', activa: true, dueno: this.duenoService.getDuenoById(10) },
    { id: 20, nombre: 'Simona', raza: 'Abisinio', edad: '3 años', fotoUrl: '/img/mascotas/20-simona.jpg', vacunas: 'Vacuna múltiple pendiente', activa: true, dueno: this.duenoService.getDuenoById(10) },
  ];

  getMascotas() {
    return this.mascotaArray;
  }

  getMascotasActivas() {
    return this.mascotaArray.filter((m) => m.activa);
  }

  getMascotasInactivas() {
    return this.mascotaArray.filter((m) => !m.activa);
  }

  getMascotaById(id: number) {
    return this.mascotaArray.find((m) => m.id === id);
  }

  // Recibe el objeto Dueno (relación como objeto) y compara por id
  getMascotasByDueno(dueno: Dueno) {
    return this.mascotaArray.filter((m) => m.dueno?.id === dueno.id);
  }

  addMascota(mascota: Mascota) {
    // Siguiente id = máximo actual + 1 (no se repite aunque se eliminen mascotas)
    mascota.id = Math.max(0, ...this.mascotaArray.map((m) => m.id)) + 1;
    this.mascotaArray.push(mascota);
  }

  updateMascota(id: number, mascota: Mascota) {
    const index = this.mascotaArray.findIndex((m) => m.id === id);
    if (index !== -1) {
      this.mascotaArray[index] = { ...mascota, id };
    }
  }

  desactivarMascota(mascota: Mascota) {
    mascota.activa = false;
  }

  activarMascota(mascota: Mascota) {
    mascota.activa = true;
  }

  deleteMascota(mascota: Mascota) {
    this.registroMedicoService.deleteRegistrosByMascota(mascota.id);
    this.mascotaArray = this.mascotaArray.filter((m) => m.id !== mascota.id);
  }
}
