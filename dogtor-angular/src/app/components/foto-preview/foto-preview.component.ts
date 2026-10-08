import { Component, input } from '@angular/core';

@Component({
  selector: 'app-foto-preview',
  imports: [],
  templateUrl: './foto-preview.component.html',
  styleUrl: './foto-preview.component.scss',
})
export class FotoPreviewComponent {
  // Entrada: URL ya validada por el padre; si viene vacía no se muestra nada
  url = input<string | null | undefined>();
}
