import { Component, input } from '@angular/core';

// Indicador de carga. 'pagina' = centrado mientras llega el contenido; 'linea' = pequeño junto a un título
@Component({
  selector: 'app-cargando',
  imports: [],
  templateUrl: './cargando.component.html',
})
export class CargandoComponent {
  tamano = input<'pagina' | 'linea'>('pagina');
}
