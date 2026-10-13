import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

// Botón de envío del formulario (con spinner mientras se guarda) y enlace opcional para cancelar
@Component({
  selector: 'app-form-acciones',
  imports: [RouterLink],
  templateUrl: './form-acciones.component.html',
  styleUrl: './form-acciones.component.scss',
})
export class FormAccionesComponent {
  texto = input('Guardar');
  textoCargando = input(''); // vacío = se mantiene el mismo texto
  cargando = input(false);
  deshabilitado = input(false);
  estilo = input<'primario' | 'secundario'>('secundario');
  rutaCancelar = input<string | unknown[] | undefined>(undefined);
}
