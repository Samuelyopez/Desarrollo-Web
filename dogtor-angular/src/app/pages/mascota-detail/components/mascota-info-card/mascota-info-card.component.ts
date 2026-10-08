import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Mascota } from '../../../../models/mascota.model';
import { Dueno } from '../../../../models/dueno.model';
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
  // Entradas: la mascota (el padre ya verificó que existe) y su dueño, que se pide aparte
  mascota = input.required<Mascota>();
  dueno = input<Dueno | undefined>();
}
