import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PageTitleComponent } from '../mascota-table-page/components/page-title/page-title.component';
import { DuenoTableComponent, DuenoFila } from './components/dueno-table/dueno-table.component';
import { Dueno } from '../../models/dueno.model';
import { DuenoService } from '../../service/dueno.service';
import { MascotaService } from '../../service/mascota.service';

@Component({
  selector: 'app-dueno-table-page',
  imports: [PageTitleComponent, DuenoTableComponent, RouterLink],
  templateUrl: './dueno-table-page.component.html',
  styleUrl: './dueno-table-page.component.scss',
})
export class DuenoTablePageComponent {
  //DI
  private duenoService = inject(DuenoService);
  private mascotaService = inject(MascotaService);

  filtro = '';
  duenos: DuenoFila[] = [];
  totalDuenos = 0;

  // Mensaje flash: llega desde el formulario (history.state) o tras eliminar
  mensaje = '';
  tipoMensaje: 'success' | 'danger' = 'success';

  ngOnInit() {
    const estado = history.state as { mensaje?: string };
    if (estado?.mensaje) {
      this.mostrarMensaje(estado.mensaje, 'success');
    }
    this.actualizarLista();
  }

  buscar(texto: string) {
    this.filtro = texto;
    this.actualizarLista();
  }

  eliminarDueno(dueno: Dueno) {
    // Igual que la llave foránea en la BD: no se borra un dueño que todavía tiene mascotas
    const totalMascotas = this.mascotaService.getMascotasByDueno(dueno).length;
    if (totalMascotas > 0) {
      this.mostrarMensaje(
        `No se puede eliminar a ${dueno.nombre}: tiene ${totalMascotas} mascota(s) registrada(s). Reasígnalas o elimínalas primero.`,
        'danger',
      );
      return;
    }
    this.duenoService.deleteDueno(dueno);
    this.mostrarMensaje(`Se eliminó a ${dueno.nombre}.`, 'success');
    this.actualizarLista();
  }

  cerrarMensaje() {
    this.mensaje = '';
  }

  private mostrarMensaje(texto: string, tipo: 'success' | 'danger') {
    this.mensaje = texto;
    this.tipoMensaje = tipo;
  }

  // Arma las filas UNA vez (no en el template) para evitar el error NG0100
  private actualizarLista() {
    this.totalDuenos = this.duenoService.getDuenos().length;
    this.duenos = this.duenoService.buscarDuenos(this.filtro).map((dueno) => ({
      dueno,
      totalMascotas: this.mascotaService.getMascotasByDueno(dueno).length,
    }));
  }
}
