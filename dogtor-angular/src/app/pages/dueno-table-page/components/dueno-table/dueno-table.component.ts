import { Component, inject, input, output } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Dueno } from '../../../../models/dueno.model';

@Component({
  selector: 'app-dueno-table',
  imports: [RouterLink],
  templateUrl: './dueno-table.component.html',
  styleUrl: './dueno-table.component.scss',
})
export class DuenoTableComponent {
  //DI
  router = inject(Router);

  // Entrada: datos que manda el componente padre
  duenoArray = input<Dueno[]>([]);

  // Salida: evento que se le avisa al componente padre
  duenoEliminado = output<Dueno>();

  verDetalleDueno(dueno: Dueno) {
    this.router.navigate(['/vet/dueno', dueno.cedula]);
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
