import { Component, input } from '@angular/core';
import { Testimonio } from '../../../../models/landing.model';

@Component({
  selector: 'app-testimonio-card',
  imports: [],
  templateUrl: './testimonio-card.component.html',
  styleUrl: './testimonio-card.component.scss',
})
export class TestimonioCardComponent {
  // Entrada: el testimonio a mostrar
  testimonio = input.required<Testimonio>();

  // Posiciones de las 5 estrellas; es un arreglo fijo para no crear uno nuevo en cada detección de cambios
  readonly posiciones = [1, 2, 3, 4, 5];
}
