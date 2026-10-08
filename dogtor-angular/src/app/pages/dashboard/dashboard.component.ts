import { Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { CurrencyPipe, DatePipe, DecimalPipe, PercentPipe } from '@angular/common';
import { catchError, EMPTY, startWith, Subject, switchMap, tap } from 'rxjs';
import { Dashboard } from '../../models/dashboard.model';
import { DashboardService } from '../../service/dashboard.service';
import { mensajeError } from '../../utils/mensaje-error';

// Portal administrador (AC29): indicadores del negocio
@Component({
  selector: 'app-dashboard',
  imports: [CurrencyPipe, DatePipe, DecimalPipe, PercentPipe],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent {
  //DI
  private dashboardService = inject(DashboardService);

  dashboard: Dashboard | null = null;
  cargando = true;
  errorMensaje = '';

  // Base de las barras: las unidades del medicamento más usado en el mes
  maxUnidades = 1;
  margen = 0;

  private recargar$ = new Subject<void>();

  constructor() {
    // Carga inicial y botón "Actualizar". switchMap descarta la respuesta vieja si se pulsa dos veces;
    // catchError va adentro para que el flujo siga vivo después de un error
    this.recargar$
      .pipe(
        startWith(undefined),
        tap(() => {
          this.cargando = true;
          this.errorMensaje = '';
        }),
        switchMap(() =>
          this.dashboardService.getDashboard().pipe(
            catchError((error: HttpErrorResponse) => {
              this.errorMensaje = mensajeError(error, 'No se pudo cargar el dashboard.');
              this.cargando = false;
              return EMPTY;
            }),
          ),
        ),
        takeUntilDestroyed(),
      )
      .subscribe((dashboard) => {
        this.dashboard = dashboard;
        this.maxUnidades = Math.max(1, ...dashboard.tratamientosPorMedicamento.map((f) => f.unidades));
        this.margen = dashboard.ventasTotales > 0 ? dashboard.gananciasTotales / dashboard.ventasTotales : 0;
        this.cargando = false;
      });
  }

  actualizar() {
    this.recargar$.next();
  }
}
