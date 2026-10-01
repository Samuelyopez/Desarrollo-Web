import { Component, input } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RegistroMedico } from '../../../../models/registro-medico.model';

@Component({
  selector: 'app-registro-medico-item',
  imports: [DatePipe],
  templateUrl: './registro-medico-item.component.html',
  styleUrl: './registro-medico-item.component.scss',
})
export class RegistroMedicoItemComponent {
  // Entradas: el padre ya resolvió los nombres; este componente solo pinta
  registro = input.required<RegistroMedico>();
  veterinario = input.required<string>();
  drogas = input<string[]>([]);
}
