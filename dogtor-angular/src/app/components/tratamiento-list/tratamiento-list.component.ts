import { Component, input } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { Tratamiento } from '../../models/tratamiento.model';
import { EstadoVacioComponent } from '../../components/estado-vacio/estado-vacio.component';

// Historial de tratamientos de una mascota. Lo usan el detalle del veterinario (con precios)
// y el del cliente (sin precios)
@Component({
  selector: 'app-tratamiento-list',
  imports: [DatePipe, CurrencyPipe, EstadoVacioComponent],
  templateUrl: './tratamiento-list.component.html',
})
export class TratamientoListComponent {
  tratamientos = input.required<Tratamiento[]>();
  mostrarPrecios = input(false);
}
