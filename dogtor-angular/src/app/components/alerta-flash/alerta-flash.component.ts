import { Component, input, model } from '@angular/core';

// Mensaje de confirmación o error que el usuario puede cerrar.
// model(): el padre usa [(mensaje)] y al cerrar queda en '' en los dos lados
@Component({
  selector: 'app-alerta-flash',
  imports: [],
  templateUrl: './alerta-flash.component.html',
})
export class AlertaFlashComponent {
  mensaje = model('');
  tipo = input<'success' | 'danger'>('success');

  cerrar() {
    this.mensaje.set('');
  }
}
