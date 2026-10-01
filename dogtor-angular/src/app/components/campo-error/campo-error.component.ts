import { Component, input } from '@angular/core';
import { AbstractControl } from '@angular/forms';

@Component({
  selector: 'app-campo-error',
  imports: [],
  templateUrl: './campo-error.component.html',
  styleUrl: './campo-error.component.scss',
})
export class CampoErrorComponent {
  // Entradas: el control a vigilar y el texto de cada error (clave = nombre del validador)
  control = input.required<AbstractControl>();
  mensajes = input<Record<string, string>>({});

  // Texto del primer error presente que tenga mensaje definido (devuelve un string, no un arreglo nuevo)
  mensaje() {
    const errores = this.control().errors;
    if (!errores) {
      return '';
    }
    const clave = Object.keys(errores).find((k) => this.mensajes()[k]);
    return clave ? this.mensajes()[clave] : '';
  }
}
