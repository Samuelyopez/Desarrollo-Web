import { Component, input } from '@angular/core';

// Encabezado de una página: sección opcional en mayúsculas (ej. "Portal cliente"), título y subtítulo
@Component({
  selector: 'app-page-title',
  imports: [],
  templateUrl: './page-title.component.html',
  styleUrl: './page-title.component.scss',
})
export class PageTitleComponent {
  seccion = input('');
  titulo = input('');
  subtitulo = input('');
}
