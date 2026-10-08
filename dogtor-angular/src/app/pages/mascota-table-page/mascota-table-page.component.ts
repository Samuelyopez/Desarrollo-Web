import { Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import {
  catchError,
  combineLatest,
  concatMap,
  debounceTime,
  distinctUntilChanged,
  EMPTY,
  map,
  startWith,
  Subject,
  switchMap,
} from 'rxjs';
import { PageTitleComponent } from './components/page-title/page-title.component';
import { MascotaTableComponent } from './components/mascota-table/mascota-table.component';
import { Mascota } from '../../models/mascota.model';
import { MascotaService } from '../../service/mascota.service';
import { mensajeError } from '../../utils/mensaje-error';

@Component({
  selector: 'app-mascota-table-page',
  imports: [PageTitleComponent, MascotaTableComponent, RouterLink],
  templateUrl: './mascota-table-page.component.html',
  styleUrl: './mascota-table-page.component.scss',
})
export class MascotaTablePageComponent {
  //DI
  private mascotaService = inject(MascotaService);

  filtro = '';
  mascotasActivas: Mascota[] = [];
  mascotasInactivas: Mascota[] = [];
  cargando = true;

  // Mensaje flash: llega desde el formulario (history.state), tras cambiar el estado o si falla la API
  mensaje = '';
  tipoMensaje: 'success' | 'danger' = 'success';

  // Eventos de la vista convertidos en flujos (RXJS)
  private busqueda$ = new Subject<string>();
  private recargar$ = new Subject<void>();
  private cambioEstado$ = new Subject<Mascota>();

  constructor() {
    // 1. Lista: una sola petición; se separa en activas e inactivas en el front.
    //    Se vuelve a pedir cuando cambia la búsqueda (con debounce) o cuando hay que recargar
    const texto$ = this.busqueda$.pipe(
      debounceTime(300),
      map((texto) => texto.trim()),
      distinctUntilChanged(),
      startWith(''),
    );

    combineLatest([texto$, this.recargar$.pipe(startWith(undefined))])
      .pipe(
        switchMap(([texto]) => {
          this.cargando = true;
          return this.mascotaService.getMascotas(texto).pipe(catchError((error) => this.manejarError(error)));
        }),
        takeUntilDestroyed(),
      )
      .subscribe((mascotas) => {
        this.mascotasActivas = mascotas.filter((m) => m.activa);
        this.mascotasInactivas = mascotas.filter((m) => !m.activa);
        this.cargando = false;
      });

    // 2. Activar/desactivar: concatMap atiende un cambio a la vez y en orden
    this.cambioEstado$
      .pipe(
        concatMap((mascota) =>
          this.mascotaService
            .cambiarEstado(mascota.id, !mascota.activa)
            .pipe(catchError((error) => this.manejarError(error))),
        ),
        takeUntilDestroyed(),
      )
      .subscribe((mascota) => {
        const estado = mascota.activa ? 'quedó activa (en la clínica)' : 'quedó inactiva (en casa)';
        this.mostrarMensaje(`${mascota.nombre} ${estado}.`, 'success');
        this.recargar$.next();
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

  cambiarEstadoMascota(mascota: Mascota) {
    this.cambioEstado$.next(mascota);
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
