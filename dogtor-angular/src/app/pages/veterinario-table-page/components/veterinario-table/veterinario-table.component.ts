import { Component, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Veterinario } from '../../../../models/veterinario.model';
import { PersonaAvatarComponent } from '../../../../components/persona-avatar/persona-avatar.component';
import { EstadoBadgeComponent } from '../../../../components/estado-badge/estado-badge.component';
import { EstadoVacioComponent } from '../../../../components/estado-vacio/estado-vacio.component';

@Component({
  selector: 'app-veterinario-table',
  imports: [RouterLink, PersonaAvatarComponent, EstadoBadgeComponent, EstadoVacioComponent],
  templateUrl: './veterinario-table.component.html',
})
export class VeterinarioTableComponent {
  // Entrada: datos que manda el componente padre
  veterinarioArray = input<Veterinario[]>([]);

  // Salidas: el padre hace la petición (activar/desactivar o eliminar)
  estadoCambiado = output<Veterinario>();
  eliminado = output<Veterinario>();

  cambiarEstado(veterinario: Veterinario) {
    const mensaje = veterinario.activo
      ? `¿Desactivar a ${veterinario.nombre}?\n\nÚsalo por vacaciones o incapacidad: no podrá iniciar sesión ni dar tratamientos. Su historial se conserva y puedes reactivarlo cuando vuelva.`
      : `¿Reactivar a ${veterinario.nombre}? Podrá volver a iniciar sesión y dar tratamientos.`;
    if (confirm(mensaje)) {
      this.estadoCambiado.emit(veterinario);
    }
  }

  // Eliminar es definitivo: se pide confirmación antes de avisar al padre
  eliminar(veterinario: Veterinario) {
    const mensaje = `¿Eliminar a ${veterinario.nombre}?

Se borra su cuenta y ya no podrá iniciar sesión. Los tratamientos que dio se conservan en el historial de las mascotas.`;
    if (confirm(mensaje)) {
      this.eliminado.emit(veterinario);
    }
  }
}
