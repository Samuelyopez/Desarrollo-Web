import { Component, input, output } from '@angular/core';

// Caja de búsqueda con lupa. Solo avisa el texto: el debounce y la petición los hace la página (RxJS)
@Component({
  selector: 'app-buscador',
  imports: [],
  templateUrl: './buscador.component.html',
  styleUrl: './buscador.component.scss',
})
export class BuscadorComponent {
  campoId = input.required<string>();
  etiqueta = input.required<string>(); // texto para lectores de pantalla
  placeholder = input('Buscar');
  valor = input('');

  buscar = output<string>();
}
