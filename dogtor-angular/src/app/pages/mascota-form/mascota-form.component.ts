import { Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { FormControl, FormGroup, FormSubmittedEvent, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { catchError, EMPTY, filter, forkJoin, of, switchMap } from 'rxjs';
import { MascotaRequest } from '../../models/mascota.model';
import { Dueno } from '../../models/dueno.model';
import { MascotaService } from '../../service/mascota.service';
import { DuenoService } from '../../service/dueno.service';
import { CampoTextoComponent } from '../../components/campo-texto/campo-texto.component';
import { DuenoSelectComponent } from './components/dueno-select/dueno-select.component';
import { FotoPreviewComponent } from './components/foto-preview/foto-preview.component';
import { mensajeError } from '../../utils/mensaje-error';

@Component({
  selector: 'app-mascota-form',
  imports: [ReactiveFormsModule, RouterLink, CampoTextoComponent, DuenoSelectComponent, FotoPreviewComponent],
  templateUrl: './mascota-form.component.html',
  styleUrl: './mascota-form.component.scss',
})
export class MascotaFormComponent {
  //DI
  private mascotaService = inject(MascotaService);
  private duenoService = inject(DuenoService);
  private router = inject(Router);
  private activatedRoute = inject(ActivatedRoute);

  // El id de la URL identifica la mascota que se edita (no existe al crear)
  private idParam: string | undefined = this.activatedRoute.snapshot.params['id'];
  mascotaId = this.idParam ? Number(this.idParam) : undefined;
  isEdit = this.mascotaId !== undefined;

  // Si se llega desde el detalle de un dueño (?dueno=cedula) se preselecciona y se vuelve allá
  duenoInicial: string | undefined = this.activatedRoute.snapshot.queryParams['dueno'];
  rutaVolver = this.duenoInicial ? ['/vet/dueno', this.duenoInicial] : ['/vet/mascotas'];

  noEncontrada = false;
  cargando = true;
  errorMensaje = '';
  duenos: Dueno[] = [];

  mascotaForm = new FormGroup({
    nombre: new FormControl('', [
      Validators.required,
      Validators.minLength(2),
      Validators.maxLength(50),
      Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]+$/),
    ]),
    raza: new FormControl('', [Validators.maxLength(50)]),
    // Se escriben como texto y se convierten a número al guardar
    edad: new FormControl('', [Validators.pattern(/^\d{1,2}$/)]),
    peso: new FormControl('', [Validators.pattern(/^\d{1,3}([.,]\d{1,2})?$/)]),
    // Puede quedar vacía mientras un veterinario la atiende
    enfermedad: new FormControl('', [Validators.maxLength(100)]),
    // Acepta una URL externa o una imagen local del proyecto (public/img → "/img/...")
    foto: new FormControl('', [Validators.pattern(/^(https?:\/\/|\/).+/)]),
    // El control guarda el objeto Dueno completo; a la API se envía su cédula
    dueno: new FormControl<Dueno | null>(null, [Validators.required]),
  });

  // Texto de cada error por campo (clave = nombre del validador); lo pinta app-campo-error
  readonly mensajes = {
    nombre: {
      required: 'El nombre es obligatorio.',
      minlength: 'El nombre debe tener al menos 2 caracteres.',
      maxlength: 'El nombre no puede superar 50 caracteres.',
      pattern: 'El nombre solo puede contener letras.',
    },
    raza: { maxlength: 'La raza no puede superar 50 caracteres.' },
    edad: { pattern: 'La edad debe ser un número entero de años (0 a 99).' },
    peso: { pattern: 'El peso debe ser un número en kg, ej. 8.5' },
    enfermedad: { maxlength: 'La enfermedad no puede superar 100 caracteres.' },
    foto: { pattern: 'La URL debe empezar por http://, https:// o / (imagen local).' },
  };

  constructor() {
    this.cargarDatos();

    // Guardar: mismo patrón del login. Un único subscribe y la petición encadenada con switchMap
    this.mascotaForm.events
      .pipe(
        filter((e) => e instanceof FormSubmittedEvent),
        filter(() => {
          this.mascotaForm.markAllAsTouched();
          return this.mascotaForm.valid;
        }),
        switchMap(() => {
          this.cargando = true;
          this.errorMensaje = '';
          const mascota = this.armarRequest();
          const peticion$ = this.isEdit
            ? this.mascotaService.updateMascota(this.mascotaId!, mascota)
            : this.mascotaService.createMascota(mascota);
          // catchError DENTRO: si la API responde 409/400 el formulario sigue funcionando
          return peticion$.pipe(catchError((error) => this.manejarError(error, 'No se pudo guardar la mascota.')));
        }),
        takeUntilDestroyed(),
      )
      .subscribe((mascota) => {
        const mensaje = this.isEdit
          ? `Se actualizaron los datos de ${mascota.nombre}.`
          : `Se registró a ${mascota.nombre} (dueño: ${mascota.duenoNombre}).`;
        // El mensaje viaja en el state de la navegación y lo muestra la tabla
        this.router.navigate(this.rutaVolver, { state: { mensaje } });
      });
  }

  // forkJoin: los dueños del select y (al editar) la mascota llegan en paralelo;
  // con las dos respuestas se puede dejar seleccionado el dueño correcto
  private cargarDatos() {
    forkJoin({
      duenos: this.duenoService.getDuenos(),
      mascota: this.isEdit ? this.mascotaService.getMascotaById(this.mascotaId!) : of(null),
    })
      .pipe(
        catchError((error: HttpErrorResponse) => {
          if (error.status === 404 || error.status === 400) {
            this.noEncontrada = true;
            this.cargando = false;
            return EMPTY;
          }
          return this.manejarError(error, 'No se pudieron cargar los datos.');
        }),
        takeUntilDestroyed(),
      )
      .subscribe(({ duenos, mascota }) => {
        this.duenos = duenos;
        if (mascota) {
          this.mascotaForm.patchValue({
            nombre: mascota.nombre,
            raza: mascota.raza ?? '',
            edad: mascota.edad?.toString() ?? '',
            peso: mascota.peso?.toString() ?? '',
            enfermedad: mascota.enfermedad ?? '',
            foto: mascota.foto ?? '',
            dueno: duenos.find((d) => d.id === mascota.duenoId) ?? null,
          });
        } else if (this.duenoInicial) {
          this.mascotaForm.controls.dueno.setValue(duenos.find((d) => d.cedula === this.duenoInicial) ?? null);
        }
        this.cargando = false;
      });
  }

  private armarRequest(): MascotaRequest {
    const valores = this.mascotaForm.getRawValue();
    return {
      nombre: valores.nombre!.trim(),
      raza: valores.raza?.trim() || undefined,
      edad: valores.edad ? Number(valores.edad) : undefined,
      peso: valores.peso ? Number(valores.peso.replace(',', '.')) : undefined,
      enfermedad: valores.enfermedad?.trim() || undefined,
      foto: valores.foto?.trim() || undefined,
      duenoCedula: valores.dueno!.cedula,
    };
  }

  private manejarError(error: HttpErrorResponse, porDefecto: string) {
    this.cargando = false;
    this.errorMensaje = mensajeError(error, porDefecto);
    return EMPTY;
  }
}
