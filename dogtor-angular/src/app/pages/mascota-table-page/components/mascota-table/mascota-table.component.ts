import { Component, inject, input, output } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Mascota } from '../../../../models/mascota.model';
import { DuenoService } from '../../../../service/dueno.service';

@Component({
  selector: 'app-mascota-table',
  imports: [RouterLink],
  templateUrl: './mascota-table.component.html',
  styleUrl: './mascota-table.component.scss',
})
export class MascotaTableComponent {
  //DI
  router = inject(Router);
  private duenoService = inject(DuenoService);

  // Entradas: datos que manda el componente padre
  mascotaArray = input<Mascota[]>([]);
  areMascotasActivas = input<boolean>(true);

  // Salidas: eventos que se le avisan al componente padre
  estadoCambiado = output<Mascota>();
  mascotaEliminada = output<Mascota>();

  fotoPorDefecto = 'https://images.icon-icons.com/3446/PNG/512/account_profile_user_avatar_icon_219236.png';

  getNombreDueno(duenoId?: number) {
    if (duenoId === undefined) {
      return '—';
    }
    return this.duenoService.getDuenoById(duenoId)?.nombre ?? '—';
  }

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
