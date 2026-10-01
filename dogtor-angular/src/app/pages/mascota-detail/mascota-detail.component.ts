import { Component, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Mascota } from '../../models/mascota.model';
import { Dueno } from '../../models/dueno.model';
import { RegistroMedico } from '../../models/registro-medico.model';
import { MascotaService } from '../../service/mascota.service';
import { DuenoService } from '../../service/dueno.service';
import { RegistroMedicoService } from '../../service/registro-medico.service';
import { DrogaService } from '../../service/droga.service';
import { UsuarioService } from '../../service/usuario.service';

@Component({
  selector: 'app-mascota-detail',
  imports: [RouterLink, DatePipe],
  templateUrl: './mascota-detail.component.html',
  styleUrl: './mascota-detail.component.scss',
})
export class MascotaDetailComponent {
  //DI
  route = inject(ActivatedRoute);
  mascotaService = inject(MascotaService);
  duenoService = inject(DuenoService);
  registroMedicoService = inject(RegistroMedicoService);
  drogaService = inject(DrogaService);
  usuarioService = inject(UsuarioService);

  mascotaId = -1;
  mascota: Mascota | undefined;
  dueno: Dueno | undefined;
  registros: RegistroMedico[] = [];

  fotoPorDefecto = 'https://images.icon-icons.com/3446/PNG/512/account_profile_user_avatar_icon_219236.png';

  ngOnInit() {
    // 1. Obtener el id de la URL  2. Buscar la mascota  3. Cargar sus relaciones
    this.mascotaId = Number(this.route.snapshot.params['id']);
    this.mascota = this.mascotaService.getMascotaById(this.mascotaId);

    if (this.mascota) {
      if (this.mascota.duenoId !== undefined) {
        this.dueno = this.duenoService.getDuenoById(this.mascota.duenoId);
      }
      this.registros = this.registroMedicoService.getRegistrosByMascota(this.mascotaId);
    }
  }

  getNombreVeterinario(veterinarioId?: number) {
    if (veterinarioId === undefined) {
      return 'Sin asignar';
    }
    return this.usuarioService.getUsuarioById(veterinarioId)?.nombre ?? 'Sin asignar';
  }

  getNombresDrogas(drogaIds: number[]) {
    return drogaIds
      .map((id) => this.drogaService.getDrogaById(id)?.nombre)
      .filter((nombre) => !!nombre)
      .join(', ');
  }
}
