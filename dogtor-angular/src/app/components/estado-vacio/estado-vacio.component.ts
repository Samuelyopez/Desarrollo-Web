import { Component, input } from '@angular/core';

// Mensaje cuando una lista no tiene datos (ej. "No se encontraron dueños")
@Component({
  selector: 'app-estado-vacio',
  imports: [],
  templateUrl: './estado-vacio.component.html',
})
export class EstadoVacioComponent {
  mensaje = input.required<string>();
  centrado = input(false);
}
