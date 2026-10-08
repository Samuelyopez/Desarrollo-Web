import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Mascota } from '../../models/mascota.model';
import { Dueno } from '../../models/dueno.model';
import { MascotaAvatarComponent } from '../mascota-avatar/mascota-avatar.component';
import { EstadoBadgeComponent } from '../estado-badge/estado-badge.component';
import { DuenoCardComponent } from '../dueno-card/dueno-card.component';

// Ficha de la mascota. La usan el portal veterinario (con dueño y botones de gestión)
// y el portal cliente (solo los datos de la mascota)
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
  modo = input<'vet' | 'cliente'>('vet');
}
