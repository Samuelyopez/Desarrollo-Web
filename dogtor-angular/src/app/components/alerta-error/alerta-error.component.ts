import { Component, input } from '@angular/core';

// Mensaje de error. 'bloque' = alerta normal; 'formulario' = pequeña con ícono, arriba de un formulario;
// 'pagina' = centrada en una columna angosta (ej. "No se encontró la mascota")
@Component({
  selector: 'app-alerta-error',
  imports: [],
  templateUrl: './alerta-error.component.html',
})
export class AlertaErrorComponent {
  mensaje = input.required<string>();
  variante = input<'bloque' | 'formulario' | 'pagina'>('bloque');
}
