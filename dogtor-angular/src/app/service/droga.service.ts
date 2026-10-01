import { Injectable } from '@angular/core';
import { Droga } from '../models/droga.model';

@Injectable({
  providedIn: 'root',
})
export class DrogaService {
  // "Base de datos" quemada con las drogas del DataInitializer de Spring Boot
  private drogaArray: Droga[] = [
    { id: 1, nombre: 'Amoxicilina', descripcion: 'Antibiótico de amplio espectro.', dosisRecomendada: '10-20 mg/kg cada 12 horas' },
    { id: 2, nombre: 'Meloxicam', descripcion: 'Antiinflamatorio no esteroideo.', dosisRecomendada: '0.1 mg/kg cada 24 horas' },
    { id: 3, nombre: 'Ivermectina', descripcion: 'Antiparasitario de amplio espectro.', dosisRecomendada: '0.2 mg/kg dosis única' },
    { id: 4, nombre: 'Prednisona', descripcion: 'Corticoide para procesos inflamatorios y alérgicos.', dosisRecomendada: '0.5-1 mg/kg cada 24 horas' },
    { id: 5, nombre: 'Dexametasona', descripcion: 'Corticoide de acción rápida.', dosisRecomendada: '0.1-0.2 mg/kg según indicación' },
  ];

  getDrogas() {
    return this.drogaArray;
  }

  getDrogaById(id: number) {
    return this.drogaArray.find((d) => d.id === id);
  }
}
