import { Component, input } from '@angular/core';
import { Dueno } from '../../models/dueno.model';

@Component({
  selector: 'app-dueno-card',
  imports: [],
  templateUrl: './dueno-card.component.html',
  styleUrl: './dueno-card.component.scss',
})
export class DuenoCardComponent {
  // Entrada: el dueño ya cargado por la página (se pide aparte a la API); puede no estar
  dueno = input<Dueno | undefined>();
}
