import { Component, input } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { Dueno } from '../../../../models/dueno.model';
import { CampoErrorComponent } from '../../../../components/campo-error/campo-error.component';

@Component({
  selector: 'app-dueno-select',
  imports: [ReactiveFormsModule, CampoErrorComponent],
  templateUrl: './dueno-select.component.html',
  styleUrl: './dueno-select.component.scss',
})
export class DuenoSelectComponent {
  // Entradas: el control (guarda el objeto Dueno) y la lista de dueños para las opciones
  control = input.required<FormControl<Dueno | null>>();
  duenos = input.required<Dueno[]>();

  readonly mensajes = { required: 'Selecciona un dueño de la lista.' };

  // compareWith del <select>: compara dueños por id y no por referencia,
  // así al editar queda seleccionado el dueño que ya tenía la mascota
  compararDuenos = (a: Dueno | null, b: Dueno | null) => (a && b ? a.id === b.id : a === b);
}
