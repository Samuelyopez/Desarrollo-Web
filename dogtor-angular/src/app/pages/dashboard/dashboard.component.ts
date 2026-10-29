import { Component, inject, LOCALE_ID } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { CurrencyPipe, DatePipe, DecimalPipe, formatCurrency, formatNumber, formatPercent } from '@angular/common';
import { catchError, EMPTY, startWith, Subject, switchMap, tap } from 'rxjs';
import { Dashboard } from '../../models/dashboard.model';
import { DashboardService } from '../../service/dashboard.service';
import { mensajeError } from '../../utils/mensaje-error';
import { Kpi, KpiCardComponent } from '../../components/kpi-card/kpi-card.component';
import { PageTitleComponent } from '../../components/page-title/page-title.component';
import { AlertaErrorComponent } from '../../components/alerta-error/alerta-error.component';
import { CargandoComponent } from '../../components/cargando/cargando.component';
import { EstadoVacioComponent } from '../../components/estado-vacio/estado-vacio.component';

// Portal administrador (AC29): indicadores del negocio
@Component({
  selector: 'app-dashboard',
  imports: [
    CurrencyPipe,
    DatePipe,
    DecimalPipe,
    KpiCardComponent,
    PageTitleComponent,
    AlertaErrorComponent,
    CargandoComponent,
    EstadoVacioComponent,
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent {
  //DI
  private dashboardService = inject(DashboardService);
  private locale = inject(LOCALE_ID);

  dashboard: Dashboard | null = null;
  // Las 8 tarjetas se arman una vez por respuesta (no en el template) y se pintan con @for
  kpis: Kpi[] = [];
  cargando = true;
  errorMensaje = '';

  // Base de las barras: las unidades del medicamento más usado en el mes
  maxUnidades = 1;

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
        this.kpis = this.armarKpis(dashboard);
        this.maxUnidades = Math.max(1, ...dashboard.tratamientosPorMedicamento.map((f) => f.unidades));
        this.cargando = false;
      });
  }

  actualizar() {
    this.recargar$.next();
  }

  private armarKpis(d: Dashboard): Kpi[] {
    const numero = (valor: number) => formatNumber(valor, this.locale);
    const pesos = (valor: number) => formatCurrency(valor, this.locale, '$', 'COP', '1.0-0');
    const margen = d.ventasTotales > 0 ? d.gananciasTotales / d.ventasTotales : 0;
    const top = d.topMedicamentos[0];

    return [
      {
        icono: 'bi-capsule',
        titulo: 'Tratamientos del mes',
        valor: numero(d.tratamientosUltimoMes),
        detalle: `${numero(d.unidadesUltimoMes)} unidades suministradas`,
      },
      { icono: 'bi-cash-stack', titulo: 'Ventas totales', valor: pesos(d.ventasTotales), detalle: 'Precio de venta × unidades' },
      {
        icono: 'bi-graph-up-arrow',
        titulo: 'Ganancias totales',
        valor: pesos(d.gananciasTotales),
        detalle: `Margen de ${formatPercent(margen, this.locale, '1.0-1')} sobre las ventas`,
      },
      {
        icono: 'bi-trophy',
        titulo: 'Más vendido',
        valor: top ? top.medicamento : '—',
        detalle: top ? `${numero(top.unidades)} unidades` : '',
        valorEsTexto: !!top,
      },
      { icono: 'bi-person-check', titulo: 'Veterinarios activos', valor: numero(d.veterinariosActivos), detalle: 'Pueden dar tratamientos' },
      { icono: 'bi-person-dash', titulo: 'Veterinarios inactivos', valor: numero(d.veterinariosInactivos), detalle: 'Vacaciones o incapacidad' },
      { icono: 'bi-heart', titulo: 'Mascotas totales', valor: numero(d.mascotasTotales), detalle: 'Registradas en la veterinaria' },
      { icono: 'bi-hospital', titulo: 'Mascotas activas', valor: numero(d.mascotasActivas), detalle: 'En la clínica (en tratamiento)' },
    ];
  }
}
