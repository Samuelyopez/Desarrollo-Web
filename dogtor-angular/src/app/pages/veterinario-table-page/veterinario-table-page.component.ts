import { Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { catchError, concatMap, debounceTime, distinctUntilChanged, EMPTY, map, startWith, Subject, switchMap } from 'rxjs';
import { PageTitleComponent } from '../mascota-table-page/components/page-title/page-title.component';
import { VeterinarioTableComponent } from './components/veterinario-table/veterinario-table.component';
import { Veterinario } from '../../models/veterinario.model';
import { VeterinarioService } from '../../service/veterinario.service';
import { mensajeError } from '../../utils/mensaje-error';

type FiltroEstado = 'todos' | 'activos' | 'inactivos';

// Portal administrador (AC26-AC28): veterinarios registrados, búsqueda y estado laboral
@Component({
  selector: 'app-veterinario-table-page',
  imports: [PageTitleComponent, VeterinarioTableComponent, RouterLink],
  templateUrl: './veterinario-table-page.component.html',
  styleUrl: './veterinario-table-page.component.scss',
})
export class VeterinarioTablePageComponent {
  //DI
  private veterinarioService = inject(VeterinarioService);

  filtro = '';
  filtroEstado: FiltroEstado = 'todos';
  veterinarios: Veterinario[] = [];
  // Se arma una vez por cambio (no en un getter): un arreglo nuevo en cada detección de cambios da NG0100
  veterinariosFiltrados: Veterinario[] = [];
  totalActivos = 0;
  cargando = true;

  // Mensaje flash: llega desde el formulario (history.state), tras cambiar el estado o si falla la API
  mensaje = '';
  tipoMensaje: 'success' | 'danger' = 'success';

  // Eventos de la vista convertidos en flujos (RXJS)
  private busqueda$ = new Subject<string>();
  private cambioEstado$ = new Subject<Veterinario>();

  constructor() {
    // 1. Lista: se vuelve a pedir cuando cambia la búsqueda (debounce + switchMap)
    this.busqueda$
      .pipe(
        debounceTime(300),
        map((texto) => texto.trim()),
        distinctUntilChanged(),
        startWith(''),
        switchMap((texto) => {
          this.cargando = true;
          return this.veterinarioService.getVeterinarios(texto).pipe(catchError((error) => this.manejarError(error)));
        }),
        takeUntilDestroyed(),
      )
      .subscribe((veterinarios) => {
        this.veterinarios = veterinarios;
        this.aplicarFiltro();
        this.cargando = false;
      });

    // 2. Activar/desactivar: concatMap atiende un cambio a la vez; la fila se reemplaza con la respuesta
    this.cambioEstado$
      .pipe(
        concatMap((veterinario) =>
          this.veterinarioService
            .cambiarEstado(veterinario.cedula, !veterinario.activo)
            .pipe(catchError((error) => this.manejarError(error))),
        ),
        takeUntilDestroyed(),
      )
      .subscribe((actualizado) => {
        this.veterinarios = this.veterinarios.map((v) => (v.cedula === actualizado.cedula ? actualizado : v));
        this.aplicarFiltro();
        const estado = actualizado.activo ? 'quedó activo' : 'quedó inactivo (no puede iniciar sesión)';
        this.mostrarMensaje(`${actualizado.nombre} ${estado}.`, 'success');
      });
  }

  ngOnInit() {
    const estado = history.state as { mensaje?: string };
    if (estado?.mensaje) {
      this.mostrarMensaje(estado.mensaje, 'success');
    }
  }

  buscar(texto: string) {
    this.filtro = texto;
    this.busqueda$.next(texto);
  }

  cambiarFiltroEstado(valor: string) {
    this.filtroEstado = valor as FiltroEstado;
    this.aplicarFiltro();
  }

  // El filtro de estado se aplica en el front: la lista es pequeña
  private aplicarFiltro() {
    const activos = this.filtroEstado === 'activos';
    this.veterinariosFiltrados =
      this.filtroEstado === 'todos' ? this.veterinarios : this.veterinarios.filter((v) => v.activo === activos);
    this.totalActivos = this.veterinarios.filter((v) => v.activo).length;
  }

  cambiarEstadoVeterinario(veterinario: Veterinario) {
    this.cambioEstado$.next(veterinario);
  }

  cerrarMensaje() {
    this.mensaje = '';
  }

  private mostrarMensaje(texto: string, tipo: 'success' | 'danger') {
    this.mensaje = texto;
    this.tipoMensaje = tipo;
  }

  // Muestra el error y devuelve EMPTY para que el flujo siga vivo
  private manejarError(error: HttpErrorResponse) {
    this.cargando = false;
    this.mostrarMensaje(mensajeError(error), 'danger');
    return EMPTY;
  }
}
