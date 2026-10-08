import { Component, input } from '@angular/core';

@Component({
  selector: 'app-estado-badge',
  imports: [],
  templateUrl: './estado-badge.component.html',
  styleUrl: './estado-badge.component.scss',
})
export class EstadoBadgeComponent {
  // Entrada: true = Activa, false = Inactiva. Los textos cambian en el portal cliente
  activa = input.required<boolean>();
  textoActiva = input('Activa');
  textoInactiva = input('Inactiva');
}
