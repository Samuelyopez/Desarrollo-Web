import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Dueno } from '../../models/dueno.model';
import { Mascota } from '../../models/mascota.model';
import { DuenoService } from '../../service/dueno.service';
import { MascotaService } from '../../service/mascota.service';
import { DuenoCardComponent } from '../../components/dueno-card/dueno-card.component';
import { MascotaAvatarComponent } from '../../components/mascota-avatar/mascota-avatar.component';
import { EstadoBadgeComponent } from '../../components/estado-badge/estado-badge.component';

@Component({
  selector: 'app-dueno-detail',
  imports: [RouterLink, DuenoCardComponent, MascotaAvatarComponent, EstadoBadgeComponent],
  templateUrl: './dueno-detail.component.html',
  styleUrl: './dueno-detail.component.scss',
})
export class DuenoDetailComponent {
  //DI
  route = inject(ActivatedRoute);
  duenoService = inject(DuenoService);
  mascotaService = inject(MascotaService);

  duenoId = -1;
  dueno: Dueno | undefined;

  // Se arma UNA vez en ngOnInit (evita el error NG0100)
  mascotas: Mascota[] = [];

  ngOnInit() {
    // 1. Obtener el id de la URL  2. Buscar el dueño  3. Traer sus mascotas
    this.duenoId = Number(this.route.snapshot.params['id']);
    this.dueno = this.duenoService.getDuenoById(this.duenoId);

    if (this.dueno) {
      this.mascotas = this.mascotaService.getMascotasByDueno(this.dueno);
    }
  }
}
