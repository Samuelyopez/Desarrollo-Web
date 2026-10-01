import { Component, input } from '@angular/core';
import { Dueno } from '../../models/dueno.model';

@Component({
  selector: 'app-dueno-card',
  imports: [],
  templateUrl: './dueno-card.component.html',
  styleUrl: './dueno-card.component.scss',
})
export class DuenoCardComponent {
  // Entrada: el dueño ya viene dentro de la mascota (objeto, no id); puede no tener
  dueno = input<Dueno | undefined>();
}
