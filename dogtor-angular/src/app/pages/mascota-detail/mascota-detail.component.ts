import { Component, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Mascota } from '../../models/mascota.model';
import { RegistroMedico } from '../../models/registro-medico.model';
import { MascotaService } from '../../service/mascota.service';
import { RegistroMedicoService } from '../../service/registro-medico.service';
import { DrogaService } from '../../service/droga.service';
import { UsuarioService } from '../../service/usuario.service';
import { MascotaAvatarComponent } from '../../components/mascota-avatar/mascota-avatar.component';
import { EstadoBadgeComponent } from '../../components/estado-badge/estado-badge.component';

@Component({
  selector: 'app-mascota-detail',
  imports: [RouterLink, DatePipe, MascotaAvatarComponent, EstadoBadgeComponent],
  templateUrl: './mascota-detail.component.html',
  styleUrl: './mascota-detail.component.scss',
})
export class MascotaDetailComponent {
  //DI
  route = inject(ActivatedRoute);
  mascotaService = inject(MascotaService);
  registroMedicoService = inject(RegistroMedicoService);
  drogaService = inject(DrogaService);
  usuarioService = inject(UsuarioService);

  mascotaId = -1;
  mascota: Mascota | undefined;
  registros: RegistroMedico[] = [];

  // El dueño ya viene dentro de la mascota (objeto, no id): no hay que buscarlo por id
  get dueno() {
    return this.mascota?.dueno;
  }

  ngOnInit() {
    // 1. Obtener el id de la URL  2. Buscar la mascota  3. Cargar su historial
    this.mascotaId = Number(this.route.snapshot.params['id']);
    this.mascota = this.mascotaService.getMascotaById(this.mascotaId);

    if (this.mascota) {
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
