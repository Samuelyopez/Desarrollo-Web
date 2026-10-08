import { Component, inject, input, output } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Dueno } from '../../../../models/dueno.model';

// Fila de la tabla: el dueño y cuántas mascotas tiene (lo calcula la página)
export interface DuenoFila {
  dueno: Dueno;
  totalMascotas: number;
}

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
  duenoArray = input<DuenoFila[]>([]);

  // Salida: evento que se le avisa al componente padre
  duenoEliminado = output<Dueno>();

  verDetalleDueno(dueno: Dueno) {
    this.router.navigate(['/dueno', dueno.id]);
  }

  eliminarDueno(dueno: Dueno) {
    if (confirm(`¿Eliminar a ${dueno.nombre}?`)) {
      this.duenoEliminado.emit(dueno);
    }
  }
}
