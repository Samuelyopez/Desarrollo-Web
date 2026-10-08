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
import { PageTitleComponent } from '../mascota-table-page/components/page-title/page-title.component';
import { DuenoTableComponent } from './components/dueno-table/dueno-table.component';
import { Dueno } from '../../models/dueno.model';
import { DuenoService } from '../../service/dueno.service';
import { mensajeError } from '../../utils/mensaje-error';

@Component({
  selector: 'app-dueno-table-page',
  imports: [PageTitleComponent, DuenoTableComponent, RouterLink],
  templateUrl: './dueno-table-page.component.html',
  styleUrl: './dueno-table-page.component.scss',
})
export class DuenoTablePageComponent {
  //DI
  private duenoService = inject(DuenoService);

  filtro = '';
  duenos: Dueno[] = [];
  cargando = true;

  // Mensaje flash: llega desde el formulario (history.state), tras eliminar o si falla la API
  mensaje = '';
  tipoMensaje: 'success' | 'danger' = 'success';

  // Eventos de la vista convertidos en flujos (RXJS)
  private busqueda$ = new Subject<string>();
  private recargar$ = new Subject<void>();
  private eliminar$ = new Subject<Dueno>();

  constructor() {
    // 1. Lista de dueños: se vuelve a pedir cuando cambia la búsqueda o cuando hay que recargar.
    //    debounceTime espera a que el usuario deje de escribir y switchMap descarta respuestas viejas
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
          return this.duenoService.getDuenos(texto).pipe(catchError((error) => this.manejarError(error)));
        }),
        takeUntilDestroyed(),
      )
      .subscribe((duenos) => {
        this.duenos = duenos;
        this.cargando = false;
      });

    // 2. Eliminación: concatMap atiende un borrado a la vez y en orden
    this.eliminar$
      .pipe(
        concatMap((dueno) =>
          this.duenoService.deleteDueno(dueno.cedula).pipe(
            map(() => dueno),
            catchError((error) => this.manejarError(error)),
          ),
        ),
        takeUntilDestroyed(),
      )
      .subscribe((dueno) => {
        const mascotas = dueno.cantidadMascotas ? ` y sus ${dueno.cantidadMascotas} mascota(s)` : '';
        this.mostrarMensaje(`Se eliminó a ${dueno.nombre}${mascotas}.`, 'success');
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

  eliminarDueno(dueno: Dueno) {
    this.eliminar$.next(dueno);
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
