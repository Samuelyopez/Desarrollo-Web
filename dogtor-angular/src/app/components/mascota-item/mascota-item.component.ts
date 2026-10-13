import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Mascota } from '../../models/mascota.model';
import { MascotaAvatarComponent } from '../mascota-avatar/mascota-avatar.component';
import { EstadoBadgeComponent } from '../estado-badge/estado-badge.component';

// Fila de una mascota en una lista (foto, nombre, raza, edad y estado) que lleva a su detalle
@Component({
  selector: 'app-mascota-item',
  imports: [RouterLink, MascotaAvatarComponent, EstadoBadgeComponent],
  templateUrl: './mascota-item.component.html',
  styleUrl: './mascota-item.component.scss',
})
export class MascotaItemComponent {
  mascota = input.required<Mascota>();
  ruta = input.required<string | unknown[]>();
}
