import { Injectable, inject } from '@angular/core';
import { Mascota } from '../models/mascota.model';
import { Dueno } from '../models/dueno.model';
import { DuenoService } from './dueno.service';

@Injectable({
  providedIn: 'root',
})
export class MascotaService {
  //DI
  // Debe ir ANTES de mascotaArray: los inicializadores de campos se ejecutan en orden
  // y el arreglo de abajo ya usa duenoService para guardar el objeto Dueno
  private duenoService = inject(DuenoService);

  // "Base de datos" quemada con las mismas mascotas del DataLoader de Spring Boot.
  // En el Sprint 3 se reemplaza por peticiones HTTP a la API
  private mascotaArray: Mascota[] = [
    this.mascota(1, 'Max', 'Golden Retriever', 1, 8.5, 'Parvovirus', '/img/mascotas/1-max.jpg', true, 1),
    this.mascota(2, 'Luna', 'Gato Siamés', 1, 3.2, 'Infección urinaria', '/img/mascotas/2-luna.jpg', true, 1),
    this.mascota(3, 'Rocky', 'Bulldog Francés', 1, 11.0, 'Dificultad respiratoria', '/img/mascotas/3-rocky.jpg', true, 2),
    this.mascota(4, 'Bella', 'Gato Persa', 3, 4.1, 'Otitis', '/img/mascotas/4-bella.jpg', true, 2),
    this.mascota(5, 'Toby', 'Poodle', 2, 6.3, 'Dermatitis', '/img/mascotas/5-toby.jpg', true, 3),
    this.mascota(6, 'Oreo', 'Gato Naranja Común', 7, 5.0, 'Insuficiencia renal', '/img/mascotas/6-oreo.jpg', true, 3),
    this.mascota(7, 'Simba', 'Golden Retriever', 4, 30.2, 'Displasia de cadera', '/img/mascotas/7-simba.jpg', true, 4),
    this.mascota(8, 'Felix', 'Gato Común Europeo', 1, 2.8, 'Gastroenteritis', '/img/mascotas/8-felix.jpg', false, 4),
    this.mascota(9, 'Milo', 'Labrador Retriever', 1, 9.0, 'Moquillo', '/img/mascotas/9-milo.jpg', true, 5),
    this.mascota(10, 'Pelusa', 'Gato Negro', 1, 3.5, '', '/img/mascotas/10-pelusa.jpg', true, 5),
    this.mascota(11, 'Thor', 'Husky Siberiano', 1, 18.4, 'Fractura de pata', '/img/mascotas/11-thor.jpg', true, 6),
    this.mascota(12, 'Michi', 'Gato Atigrado', 3, 4.6, 'Conjuntivitis', '/img/mascotas/12-michi.jpg', true, 6),
    this.mascota(13, 'Buddy', 'Pastor Alemán', 2, 28.0, 'Leishmaniasis', '/img/mascotas/13-buddy.jpg', true, 7),
    this.mascota(14, 'Salem', 'Gato Naranja', 7, 5.4, 'Diabetes', '/img/mascotas/14-salem.jpg', true, 7),
    this.mascota(15, 'Duke', 'Mestizo', 4, 15.7, 'Parásitos intestinales', '/img/mascotas/15-duke.jpg', true, 8),
    this.mascota(16, 'Whiskers', 'Ragdoll', 1, 3.0, 'Gripe felina', '/img/mascotas/16-whiskers.jpg', true, 8),
    this.mascota(17, 'Rex', 'Chihuahua', 1, 1.8, 'Hipoglucemia', '/img/mascotas/17-rex.jpg', true, 9),
    this.mascota(18, 'Nala', 'Gato Mestizo', 1, 3.3, 'Herida por mordedura', '/img/mascotas/18-nala.jpg', true, 9),
    this.mascota(19, 'Zeus', 'Pug', 1, 7.2, 'Alergia alimentaria', '/img/mascotas/19-zeus.jpg', true, 10),
    this.mascota(20, 'Simona', 'Abisinio', 3, 3.9, 'Anemia', '/img/mascotas/20-simona.jpg', true, 10),
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
    this.mascotaArray = this.mascotaArray.filter((m) => m.id !== mascota.id);
  }

  private mascota(
    id: number,
    nombre: string,
    raza: string,
    edad: number,
    peso: number,
    enfermedad: string,
    foto: string,
    activa: boolean,
    duenoId: number,
  ): Mascota {
    return { id, nombre, raza, edad, peso, enfermedad, foto, activa, duenoId, dueno: this.duenoService.getDuenoById(duenoId) };
  }
}
