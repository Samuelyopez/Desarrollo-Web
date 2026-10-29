import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

// Enlace "← Volver a ..." al inicio de las páginas de detalle
@Component({
  selector: 'app-volver-link',
  imports: [RouterLink],
  templateUrl: './volver-link.component.html',
  styleUrl: './volver-link.component.scss',
})
export class VolverLinkComponent {
  ruta = input.required<string | unknown[]>();
  texto = input.required<string>();
}
