import { Component, input } from '@angular/core';

@Component({
  selector: 'app-section-header',
  imports: [],
  templateUrl: './section-header.component.html',
  styleUrl: './section-header.component.scss',
})
export class SectionHeaderComponent {
  titulo = input.required<string>();
  subtitulo = input<string>('');
  // Ancla opcional para navegar o hacer scroll a la sección. Se llama "ancla" (no "id")
  // para que Angular no repita el id en el host del componente
  ancla = input<string | undefined>();
}
