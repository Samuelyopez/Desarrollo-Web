import { Component, inject, input, output } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Mascota } from '../../../../models/mascota.model';
import { MascotaAvatarComponent } from '../../../../components/mascota-avatar/mascota-avatar.component';
import { EstadoVacioComponent } from '../../../../components/estado-vacio/estado-vacio.component';
import { injectPortal } from '../../../../utils/portal';

@Component({
  selector: 'app-mascota-table',
  imports: [RouterLink, MascotaAvatarComponent, EstadoVacioComponent],
  templateUrl: './mascota-table.component.html',
  styleUrl: './mascota-table.component.scss',
})
export class MascotaTableComponent {
  //DI
  portal = injectPortal();
  router = inject(Router);

  // Entradas: datos que manda el componente padre
  mascotaArray = input<Mascota[]>([]);
  areMascotasActivas = input<boolean>(true);

  // Salida: evento que se le avisa al componente padre.
  // No hay "eliminar": las mascotas solo se activan o desactivan
  estadoCambiado = output<Mascota>();

  verDetalleMascota(mascota: Mascota) {
    this.router.navigate([this.portal.base + '/mascota', mascota.id]);
  }

  cambiarEstado(mascota: Mascota) {
    this.estadoCambiado.emit(mascota);
  }
}
