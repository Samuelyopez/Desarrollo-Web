import { Injectable, inject } from '@angular/core';
import { Mascota } from '../models/mascota.model';
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
    { id: 1, nombre: 'Max', raza: 'Golden Retriever', edad: '2 meses', fotoUrl: 'https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&w=300&q=80', vacunas: 'Al día', activa: true, duenoId: 1, dueno: this.duenoService.getDuenoById(1) },
    { id: 2, nombre: 'Luna', raza: 'Gato Siamés', edad: '1 año', fotoUrl: 'https://images.unsplash.com/photo-1513360371669-4adf3dd7dff8?auto=format&fit=crop&w=300&q=80', vacunas: 'Falta vacuna de rabia', activa: true, duenoId: 1, dueno: this.duenoService.getDuenoById(1) },
    { id: 3, nombre: 'Rocky', raza: 'Bulldog Francés', edad: '8 meses', fotoUrl: 'https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?auto=format&fit=crop&w=300&q=80', vacunas: 'Falta refuerzo anual', activa: true, duenoId: 2, dueno: this.duenoService.getDuenoById(2) },
    { id: 4, nombre: 'Bella', raza: 'Gato Persa', edad: '3 años', fotoUrl: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=300&q=80', vacunas: 'Desparasitado', activa: true, duenoId: 2, dueno: this.duenoService.getDuenoById(2) },
    { id: 5, nombre: 'Toby', raza: 'Poodle', edad: '1 año y medio', fotoUrl: 'https://images.unsplash.com/photo-1591768575198-88dac53fbd0a?auto=format&fit=crop&w=300&q=80', vacunas: 'Falta vacuna de rabia', activa: true, duenoId: 3, dueno: this.duenoService.getDuenoById(3) },
    { id: 6, nombre: 'Oreo', raza: 'Gato Naranja Común', edad: '7 años', fotoUrl: 'https://images.unsplash.com/photo-1573865526739-10659fec78a5?auto=format&fit=crop&w=300&q=80', vacunas: 'Esquema completo', activa: true, duenoId: 3, dueno: this.duenoService.getDuenoById(3) },
    { id: 7, nombre: 'Simba', raza: 'Golden Retriever', edad: '4 años', fotoUrl: 'https://images.unsplash.com/photo-1591160690555-5debfba289f0?auto=format&fit=crop&w=300&q=80', vacunas: 'Desparasitado', activa: true, duenoId: 4, dueno: this.duenoService.getDuenoById(4) },
    { id: 8, nombre: 'Felix', raza: 'Gato Común Europeo', edad: '6 meses', fotoUrl: 'https://images.unsplash.com/photo-1543852786-1cf6624b9987?auto=format&fit=crop&w=300&q=80', vacunas: 'Vacuna múltiple pendiente', activa: false, duenoId: 4, dueno: this.duenoService.getDuenoById(4) },
    { id: 9, nombre: 'Milo', raza: 'Labrador Retriever', edad: '2 meses', fotoUrl: 'https://images.unsplash.com/photo-1611003228941-98852ba62227?auto=format&fit=crop&w=300&q=80', vacunas: 'Esquema completo', activa: true, duenoId: 5, dueno: this.duenoService.getDuenoById(5) },
    { id: 10, nombre: 'Pelusa', raza: 'Gato Negro', edad: '1 año', fotoUrl: 'https://images.unsplash.com/photo-1546527868-ccb7ee7dfa6a?auto=format&fit=crop&w=300&q=80', vacunas: 'Al día', activa: true, duenoId: 5, dueno: this.duenoService.getDuenoById(5) },
    { id: 11, nombre: 'Thor', raza: 'Husky Siberiano', edad: '8 meses', fotoUrl: 'https://images.unsplash.com/photo-1568572933382-74d440642117?auto=format&fit=crop&w=300&q=80', vacunas: 'Vacuna múltiple pendiente', activa: true, duenoId: 6, dueno: this.duenoService.getDuenoById(6) },
    { id: 12, nombre: 'Michi', raza: 'Gato Atigrado', edad: '3 años', fotoUrl: 'https://images.unsplash.com/photo-1495360010541-f48722b34f7d?auto=format&fit=crop&w=300&q=80', vacunas: 'Falta refuerzo anual', activa: true, duenoId: 6, dueno: this.duenoService.getDuenoById(6) },
    { id: 13, nombre: 'Buddy', raza: 'Pastor Alemán', edad: '1 año y medio', fotoUrl: 'https://images.unsplash.com/photo-1589952283406-b53a7d1347e8?auto=format&fit=crop&w=300&q=80', vacunas: 'Al día', activa: true, duenoId: 7, dueno: this.duenoService.getDuenoById(7) },
    { id: 14, nombre: 'Salem', raza: 'Gato Naranja', edad: '7 años', fotoUrl: 'https://images.unsplash.com/photo-1592194996308-7b43878e84a6?auto=format&fit=crop&w=300&q=80', vacunas: 'Falta vacuna de rabia', activa: true, duenoId: 7, dueno: this.duenoService.getDuenoById(7) },
    { id: 15, nombre: 'Duke', raza: 'Mestizo', edad: '4 años', fotoUrl: 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=300&q=80', vacunas: 'Falta refuerzo anual', activa: true, duenoId: 8, dueno: this.duenoService.getDuenoById(8) },
    { id: 16, nombre: 'Whiskers', raza: 'Ragdoll', edad: '6 meses', fotoUrl: 'https://images.unsplash.com/photo-1526336024174-e58f5cdd8e13?auto=format&fit=crop&w=300&q=80', vacunas: 'Desparasitado', activa: true, duenoId: 8, dueno: this.duenoService.getDuenoById(8) },
    { id: 17, nombre: 'Rex', raza: 'Chihuahua', edad: '2 meses', fotoUrl: 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&w=300&q=80', vacunas: 'Falta vacuna de rabia', activa: true, duenoId: 9, dueno: this.duenoService.getDuenoById(9) },
    { id: 18, nombre: 'Nala', raza: 'Gato Mestizo', edad: '1 año', fotoUrl: 'https://images.unsplash.com/photo-1615789591457-74a63395c990?auto=format&fit=crop&w=300&q=80', vacunas: 'Esquema completo', activa: true, duenoId: 9, dueno: this.duenoService.getDuenoById(9) },
    { id: 19, nombre: 'Zeus', raza: 'Pug', edad: '8 meses', fotoUrl: 'https://images.unsplash.com/photo-1517423440428-a5a00ad493e8?auto=format&fit=crop&w=300&q=80', vacunas: 'Desparasitado', activa: true, duenoId: 10, dueno: this.duenoService.getDuenoById(10) },
    { id: 20, nombre: 'Simona', raza: 'Abisinio', edad: '3 años', fotoUrl: 'https://images.unsplash.com/photo-1571566882372-1598d88abd90?auto=format&fit=crop&w=300&q=80', vacunas: 'Vacuna múltiple pendiente', activa: true, duenoId: 10, dueno: this.duenoService.getDuenoById(10) },
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

  getMascotasByDueno(duenoId: number) {
    return this.mascotaArray.filter((m) => m.duenoId === duenoId);
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
