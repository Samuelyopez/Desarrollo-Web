import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Mascota } from '../../../../models/mascota.model';
import { MascotaAvatarComponent } from '../../../../components/mascota-avatar/mascota-avatar.component';
import { EstadoBadgeComponent } from '../../../../components/estado-badge/estado-badge.component';
import { DuenoCardComponent } from '../../../../components/dueno-card/dueno-card.component';

@Component({
  selector: 'app-mascota-info-card',
  imports: [RouterLink, MascotaAvatarComponent, EstadoBadgeComponent, DuenoCardComponent],
  templateUrl: './mascota-info-card.component.html',
  styleUrl: './mascota-info-card.component.scss',
})
export class MascotaInfoCardComponent {
  // Entrada: la mascota a mostrar (el padre ya verificó que existe)
  mascota = input.required<Mascota>();
}
