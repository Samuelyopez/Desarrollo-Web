import { Component, input } from '@angular/core';

// Marco de los formularios: fondo, tarjeta centrada y título. El formulario va adentro (ng-content)
@Component({
  selector: 'app-form-card',
  imports: [],
  templateUrl: './form-card.component.html',
  styleUrl: './form-card.component.scss',
})
export class FormCardComponent {
  titulo = input.required<string>();
  subtitulo = input('');
  icono = input(''); // si viene, se muestra grande encima del título (ej. login)
  ancho = input<'angosto' | 'normal' | 'ancho'>('normal');

  readonly columnas = {
    angosto: 'col-md-7 col-lg-4',
    normal: 'col-md-8 col-lg-5',
    ancho: 'col-md-9 col-lg-6',
  };
}
