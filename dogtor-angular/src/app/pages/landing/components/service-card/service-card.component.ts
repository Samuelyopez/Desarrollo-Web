import { Component, input } from '@angular/core';
import { Servicio } from '../../../../models/landing.model';

@Component({
  selector: 'app-service-card',
  imports: [],
  templateUrl: './service-card.component.html',
  styleUrl: './service-card.component.scss',
})
export class ServiceCardComponent {
  // Entrada: el servicio a mostrar
  servicio = input.required<Servicio>();
}
