import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Mascota } from '../../../../models/mascota.model';
import { MascotaAvatarComponent } from '../../../../components/mascota-avatar/mascota-avatar.component';
import { EstadoBadgeComponent } from '../../../../components/estado-badge/estado-badge.component';

// Tarjeta de una mascota en el portal cliente
@Component({
  selector: 'app-mascota-card',
  imports: [RouterLink, MascotaAvatarComponent, EstadoBadgeComponent],
  templateUrl: './mascota-card.component.html',
  styleUrl: './mascota-card.component.scss',
})
export class MascotaCardComponent {
  mascota = input.required<Mascota>();
}
