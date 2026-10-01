import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PageTitleComponent } from './components/page-title/page-title.component';
import { MascotaTableComponent } from './components/mascota-table/mascota-table.component';
import { Mascota } from '../../models/mascota.model';
import { MascotaService } from '../../service/mascota.service';

@Component({
  selector: 'app-mascota-table-page',
  imports: [PageTitleComponent, MascotaTableComponent, RouterLink],
  templateUrl: './mascota-table-page.component.html',
  styleUrl: './mascota-table-page.component.scss',
})
export class MascotaTablePageComponent {
  //DI
  private mascotaService = inject(MascotaService);

  mascotasActivas: Mascota[] = [];
  mascotasInactivas: Mascota[] = [];

  ngOnInit() {
    this.actualizarListas();
  }

  cambiarEstadoMascota(mascota: Mascota) {
    if (mascota.activa) {
      this.mascotaService.desactivarMascota(mascota);
    } else {
      this.mascotaService.activarMascota(mascota);
    }
    this.actualizarListas();
  }

  eliminarMascota(mascota: Mascota) {
    this.mascotaService.deleteMascota(mascota);
    this.actualizarListas();
  }

  // Vuelve a pedir los datos al servicio para refrescar la vista
  private actualizarListas() {
    this.mascotasActivas = this.mascotaService.getMascotasActivas();
    this.mascotasInactivas = this.mascotaService.getMascotasInactivas();
  }
}
