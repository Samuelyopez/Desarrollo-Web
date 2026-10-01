import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-cta-banner',
  imports: [RouterLink],
  templateUrl: './cta-banner.component.html',
  styleUrl: './cta-banner.component.scss',
})
export class CtaBannerComponent {
  // Entradas: textos del banner y la ruta interna a la que lleva el botón
  titulo = input.required<string>();
  texto = input<string>('');
  textoBoton = input.required<string>();
  ruta = input.required<string>();
}
