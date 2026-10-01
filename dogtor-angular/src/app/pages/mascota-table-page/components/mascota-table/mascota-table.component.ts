import { Component, inject, input, output } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Mascota } from '../../../../models/mascota.model';
import { MascotaAvatarComponent } from '../../../../components/mascota-avatar/mascota-avatar.component';

@Component({
  selector: 'app-mascota-table',
  imports: [RouterLink, MascotaAvatarComponent],
  templateUrl: './mascota-table.component.html',
  styleUrl: './mascota-table.component.scss',
})
export class MascotaTableComponent {
  //DI
  router = inject(Router);

  // Entradas: datos que manda el componente padre
  mascotaArray = input<Mascota[]>([]);
  areMascotasActivas = input<boolean>(true);

  // Salidas: eventos que se le avisan al componente padre
  estadoCambiado = output<Mascota>();
  mascotaEliminada = output<Mascota>();

  verDetalleMascota(mascota: Mascota) {
    this.router.navigate(['/mascota', mascota.id]);
  }

  cambiarEstado(mascota: Mascota) {
    this.estadoCambiado.emit(mascota);
  }

  eliminarMascota(mascota: Mascota) {
    if (confirm(`¿Eliminar a ${mascota.nombre}? También se borrará su historial médico.`)) {
      this.mascotaEliminada.emit(mascota);
    }
  }
}
