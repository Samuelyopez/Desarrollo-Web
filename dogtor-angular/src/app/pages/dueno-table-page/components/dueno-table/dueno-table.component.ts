import { Component, inject, input, output } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Dueno } from '../../../../models/dueno.model';
import { EstadoVacioComponent } from '../../../../components/estado-vacio/estado-vacio.component';
import { injectPortal } from '../../../../utils/portal';

@Component({
  selector: 'app-dueno-table',
  imports: [RouterLink, EstadoVacioComponent],
  templateUrl: './dueno-table.component.html',
})
export class DuenoTableComponent {
  //DI
  portal = injectPortal();
  router = inject(Router);

  // Entrada: datos que manda el componente padre
  duenoArray = input<Dueno[]>([]);

  // Salida: evento que se le avisa al componente padre
  duenoEliminado = output<Dueno>();

  verDetalleDueno(dueno: Dueno) {
    this.router.navigate([this.portal.base + '/dueno', dueno.cedula]);
  }

  // Eliminación en cascada: se avisa cuántas mascotas se van con el dueño
  eliminarDueno(dueno: Dueno) {
    const total = dueno.cantidadMascotas ?? 0;
    const detalle =
      total > 0
        ? `\n\nTambién se eliminarán sus ${total} mascota(s) y su usuario. Los tratamientos se conservan en el historial.`
        : '';
    if (confirm(`¿Eliminar a ${dueno.nombre}?${detalle}`)) {
      this.duenoEliminado.emit(dueno);
    }
  }
}
