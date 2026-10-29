import { Component, input } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { CampoErrorComponent } from '../campo-error/campo-error.component';

// Lista desplegable de un formulario reactivo con su etiqueta y sus errores (equivalente a campo-texto).
// Cada página dice qué texto mostrar y qué valor guardar de cada opción
@Component({
  selector: 'app-campo-select',
  imports: [ReactiveFormsModule, CampoErrorComponent],
  templateUrl: './campo-select.component.html',
})
export class CampoSelectComponent<T> {
  campoId = input.required<string>();
  etiqueta = input.required<string>();
  control = input.required<FormControl>();
  opciones = input.required<T[]>();
  textoOpcion = input.required<(opcion: T) => string>();
  // Por defecto el control guarda la opción completa; ej. (m) => m.id para guardar solo el id
  valorOpcion = input<(opcion: T) => unknown>((opcion) => opcion);
  // compareWith del <select>: al editar deja seleccionada la opción aunque sea otro objeto igual
  compararCon = input<(a: any, b: any) => boolean>((a, b) => a === b);
  placeholder = input('Selecciona una opción...');
  mensajes = input<Record<string, string>>({});
}
