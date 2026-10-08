import { Component, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Veterinario } from '../../../../models/veterinario.model';
import { PersonaAvatarComponent } from '../../../../components/persona-avatar/persona-avatar.component';
import { EstadoBadgeComponent } from '../../../../components/estado-badge/estado-badge.component';

@Component({
  selector: 'app-veterinario-table',
  imports: [RouterLink, PersonaAvatarComponent, EstadoBadgeComponent],
  templateUrl: './veterinario-table.component.html',
  styleUrl: './veterinario-table.component.scss',
})
export class VeterinarioTableComponent {
  // Entrada: datos que manda el componente padre
  veterinarioArray = input<Veterinario[]>([]);

  // Salida: el padre hace la petición. No hay "eliminar": solo activar o desactivar (estado laboral)
  estadoCambiado = output<Veterinario>();

  cambiarEstado(veterinario: Veterinario) {
    const mensaje = veterinario.activo
      ? `¿Desactivar a ${veterinario.nombre}?\n\nÚsalo por vacaciones o incapacidad: no podrá iniciar sesión ni dar tratamientos. Su historial se conserva y puedes reactivarlo cuando vuelva.`
      : `¿Reactivar a ${veterinario.nombre}? Podrá volver a iniciar sesión y dar tratamientos.`;
    if (confirm(mensaje)) {
      this.estadoCambiado.emit(veterinario);
    }
  }
}
