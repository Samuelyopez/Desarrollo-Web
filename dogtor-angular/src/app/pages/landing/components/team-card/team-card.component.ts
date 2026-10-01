import { Component, input } from '@angular/core';
import { MiembroEquipo } from '../../../../models/landing.model';

@Component({
  selector: 'app-team-card',
  imports: [],
  templateUrl: './team-card.component.html',
  styleUrl: './team-card.component.scss',
})
export class TeamCardComponent {
  // Entrada: el miembro del equipo a mostrar
  miembro = input.required<MiembroEquipo>();
}
