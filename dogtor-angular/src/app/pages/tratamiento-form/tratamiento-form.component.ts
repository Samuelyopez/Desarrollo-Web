import { Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { FormControl, FormGroup, FormSubmittedEvent, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { catchError, EMPTY, filter, forkJoin, map, switchMap } from 'rxjs';
import { Mascota } from '../../models/mascota.model';
import { Medicamento } from '../../models/medicamento.model';
import { AuthService } from '../../service/auth.service';
import { MascotaService } from '../../service/mascota.service';
import { MedicamentoService } from '../../service/medicamento.service';
import { TratamientoService } from '../../service/tratamiento.service';
import { CampoErrorComponent } from '../../components/campo-error/campo-error.component';
import { mensajeError } from '../../utils/mensaje-error';

// AC31: el veterinario logueado da un medicamento (de la lista desplegable) a una mascota activa con la fecha de hoy
@Component({
  selector: 'app-tratamiento-form',
  imports: [ReactiveFormsModule, RouterLink, CurrencyPipe, DatePipe, CampoErrorComponent],
  templateUrl: './tratamiento-form.component.html',
  styleUrl: './tratamiento-form.component.scss',
})
export class TratamientoFormComponent {
  //DI
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private mascotaService = inject(MascotaService);
  private medicamentoService = inject(MedicamentoService);
  private tratamientoService = inject(TratamientoService);

  // El veterinario es quien inició sesión y la fecha es hoy: solo se muestran (la fecha real la pone la API)
  veterinario = inject(AuthService).usuario()!;
  hoy = new Date();

  // Si se llega desde el detalle de una mascota (?mascota=id) se preselecciona y se vuelve allá
  mascotaInicial = Number(this.route.snapshot.queryParams['mascota']) || undefined;
  rutaVolver = this.mascotaInicial ? ['/vet/mascota', this.mascotaInicial] : ['/vet'];

  mascotas: Mascota[] = [];
  medicamentos: Medicamento[] = [];
  cargando = true;
  errorMensaje = '';
  avisoMascota = '';

  tratamientoForm = new FormGroup({
    mascota: new FormControl<number | null>(null, [Validators.required]),
    medicamento: new FormControl<Medicamento | null>(null, [Validators.required]),
    cantidad: new FormControl<number>(1, { nonNullable: true, validators: [Validators.required, Validators.min(1)] }),
  });

  readonly mensajes = {
    mascota: { required: 'Selecciona una mascota activa.' },
    medicamento: { required: 'Selecciona un medicamento.' },
  };
  // El máximo depende del medicamento elegido: se actualiza cuando cambia
  mensajesCantidad = this.armarMensajesCantidad(1);

  // Comparar por id: tras recargar la lista el select sigue mostrando el medicamento elegido
  compararMedicamentos = (a: Medicamento | null, b: Medicamento | null) => (a && b ? a.id === b.id : a === b);

  constructor() {
    this.cargarDatos();

    // Al cambiar de medicamento la cantidad máxima pasa a ser sus unidades disponibles
    this.tratamientoForm.controls.medicamento.valueChanges.pipe(takeUntilDestroyed()).subscribe((medicamento) => {
      const disponibles = medicamento?.unidadesDisponibles ?? 1;
      const cantidad = this.tratamientoForm.controls.cantidad;
      cantidad.setValidators([Validators.required, Validators.min(1), Validators.max(disponibles)]);
      cantidad.setValue(1);
      this.mensajesCantidad = this.armarMensajesCantidad(disponibles);
    });

    // Guardar: mismo patrón de los demás formularios (form.events + switchMap + catchError adentro)
    this.tratamientoForm.events
      .pipe(
        filter((e) => e instanceof FormSubmittedEvent),
        filter(() => {
          this.tratamientoForm.markAllAsTouched();
          return this.tratamientoForm.valid;
        }),
        switchMap(() => {
          this.cargando = true;
          this.errorMensaje = '';
          const { mascota, medicamento, cantidad } = this.tratamientoForm.getRawValue();
          return this.tratamientoService
            .createTratamiento({
              mascotaId: mascota!,
              veterinarioId: this.veterinario.perfilId,
              medicamentoId: medicamento!.id,
              cantidad,
            })
            .pipe(
              map((tratamiento) => ({ tratamiento, mascotaId: mascota! })),
              catchError((error: HttpErrorResponse) => this.manejarError(error)),
            );
        }),
        takeUntilDestroyed(),
      )
      .subscribe(({ tratamiento, mascotaId }) => {
        const mensaje = `Se registró ${tratamiento.medicamentoNombre} x${tratamiento.cantidad} para ${tratamiento.mascotaNombre}.`;
        this.router.navigate(['/vet/mascota', mascotaId], { state: { mensaje } });
      });
  }

  // Total estimado = precio de venta x cantidad
  get total() {
    const { medicamento, cantidad } = this.tratamientoForm.getRawValue();
    return medicamento ? medicamento.precioVenta * (cantidad || 0) : 0;
  }

  // forkJoin: mascotas activas y medicamentos con unidades llegan en paralelo
  private cargarDatos() {
    forkJoin({
      mascotas: this.mascotaService.getMascotas().pipe(map((mascotas) => mascotas.filter((m) => m.activa))),
      medicamentos: this.medicamentoService.getMedicamentos(true),
    })
      .pipe(
        catchError((error: HttpErrorResponse) => this.manejarError(error, false)),
        takeUntilDestroyed(),
      )
      .subscribe(({ mascotas, medicamentos }) => {
        this.mascotas = mascotas;
        this.medicamentos = medicamentos;
        if (this.mascotaInicial) {
          const existe = mascotas.some((m) => m.id === this.mascotaInicial);
          if (existe) {
            this.tratamientoForm.controls.mascota.setValue(this.mascotaInicial);
          } else {
            this.avisoMascota = 'Esa mascota no está activa: solo se puede dar tratamiento a mascotas en la clínica.';
          }
        }
        this.cargando = false;
      });
  }

  private armarMensajesCantidad(disponibles: number) {
    return {
      required: 'La cantidad es obligatoria.',
      min: 'La cantidad debe ser al menos 1.',
      max: `Solo hay ${disponibles} unidad(es) disponibles.`,
    };
  }

  // Tras un 409 (sin unidades) se recargan los medicamentos para ver el stock real
  private recargarMedicamentos() {
    this.medicamentoService
      .getMedicamentos(true)
      .pipe(catchError(() => EMPTY))
      .subscribe((medicamentos) => (this.medicamentos = medicamentos));
  }

  private manejarError(error: HttpErrorResponse, recargar = true) {
    this.cargando = false;
    this.errorMensaje = mensajeError(error, 'No se pudo registrar el tratamiento.');
    if (recargar && error.status === 409) {
      this.recargarMedicamentos();
    }
    return EMPTY;
  }
}
