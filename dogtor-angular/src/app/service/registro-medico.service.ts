import { Injectable } from '@angular/core';
import { RegistroMedico } from '../models/registro-medico.model';

@Injectable({
  providedIn: 'root',
})
export class RegistroMedicoService {
  // "Base de datos" quemada: historial clínico de algunas mascotas
  private registroArray: RegistroMedico[] = [
    { id: 1, diagnostico: 'Otitis externa leve en oído derecho.', tratamiento: 'Limpieza ótica diaria y antibiótico por 7 días.', fecha: '2026-09-02T10:15:00', mascotaId: 1, veterinarioId: 2, drogaIds: [1] },
    { id: 2, diagnostico: 'Control general: buen estado de salud.', tratamiento: 'Desparasitación preventiva.', fecha: '2026-09-15T16:40:00', mascotaId: 1, veterinarioId: 3, drogaIds: [3] },
    { id: 3, diagnostico: 'Dermatitis alérgica en zona abdominal.', tratamiento: 'Corticoide oral y cambio de alimento.', fecha: '2026-09-10T09:00:00', mascotaId: 2, veterinarioId: 2, drogaIds: [4] },
    { id: 4, diagnostico: 'Cojera en pata trasera izquierda.', tratamiento: 'Reposo por 10 días y antiinflamatorio.', fecha: '2026-09-20T11:30:00', mascotaId: 3, veterinarioId: 3, drogaIds: [2] },
  ];

  getRegistrosByMascota(mascotaId: number) {
    return this.registroArray.filter((r) => r.mascotaId === mascotaId);
  }

  // Equivale al cascade = ALL / orphanRemoval de Mascota.registros en Spring Boot
  deleteRegistrosByMascota(mascotaId: number) {
    this.registroArray = this.registroArray.filter((r) => r.mascotaId !== mascotaId);
  }
}
