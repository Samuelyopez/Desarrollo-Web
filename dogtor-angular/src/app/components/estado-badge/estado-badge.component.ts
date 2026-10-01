import { Component, input } from '@angular/core';

@Component({
  selector: 'app-estado-badge',
  imports: [],
  templateUrl: './estado-badge.component.html',
  styleUrl: './estado-badge.component.scss',
})
export class EstadoBadgeComponent {
  // Entrada: true = Activa, false = Inactiva
  activa = input.required<boolean>();
}
