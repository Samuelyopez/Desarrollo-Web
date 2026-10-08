import { Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { FormControl, FormGroup, FormSubmittedEvent, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { catchError, EMPTY, filter, switchMap } from 'rxjs';
import { VeterinarioRequest } from '../../models/veterinario.model';
import { VeterinarioService } from '../../service/veterinario.service';
import { CampoTextoComponent } from '../../components/campo-texto/campo-texto.component';
import { FotoPreviewComponent } from '../../components/foto-preview/foto-preview.component';
import { mensajeError } from '../../utils/mensaje-error';

// Portal administrador: registrar (AC26) y actualizar por cédula (AC27) un veterinario.
// El estado laboral (AC28) se cambia desde la tabla
@Component({
  selector: 'app-veterinario-form',
  imports: [ReactiveFormsModule, RouterLink, CampoTextoComponent, FotoPreviewComponent],
  templateUrl: './veterinario-form.component.html',
  styleUrl: './veterinario-form.component.scss',
})
export class VeterinarioFormComponent {
  //DI
  private veterinarioService = inject(VeterinarioService);
  private router = inject(Router);
  private activatedRoute = inject(ActivatedRoute);

  // La cédula de la URL identifica al veterinario que se edita (no existe al crear)
  cedula: string | undefined = this.activatedRoute.snapshot.params['cedula'];
  isEdit = this.cedula !== undefined;
  noEncontrado = false;
  cargando = false;
  errorMensaje = '';

  veterinarioForm = new FormGroup({
    // Solo dígitos: 6 a 10
    cedula: new FormControl('', [Validators.required, Validators.pattern(/^\d{6,10}$/)]),
    nombre: new FormControl('', [
      Validators.required,
      Validators.minLength(3),
      Validators.maxLength(80),
      Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]+$/),
    ]),
    especialidad: new FormControl('', [Validators.required, Validators.maxLength(60)]),
    // Acepta una URL externa o una imagen local del proyecto (public/img → "/img/...")
    foto: new FormControl('', [Validators.pattern(/^(https?:\/\/|\/).+/)]),
    correo: new FormControl('', [Validators.required, Validators.email]),
    // Con la que el veterinario entra al portal. Obligatoria solo al crear
    password: new FormControl('', [Validators.required, Validators.minLength(6), Validators.maxLength(50)]),
  });

  // Texto de cada error por campo (clave = nombre del validador); lo pinta app-campo-error
  readonly mensajes = {
    cedula: {
      required: 'La cédula es obligatoria.',
      pattern: 'La cédula debe tener entre 6 y 10 dígitos, sin puntos.',
    },
    nombre: {
      required: 'El nombre es obligatorio.',
      minlength: 'El nombre debe tener al menos 3 caracteres.',
      maxlength: 'El nombre no puede superar 80 caracteres.',
      pattern: 'El nombre solo puede contener letras.',
    },
    especialidad: {
      required: 'La especialidad es obligatoria.',
      maxlength: 'La especialidad no puede superar 60 caracteres.',
    },
    foto: { pattern: 'La URL debe empezar por http://, https:// o / (imagen local).' },
    correo: {
      required: 'El correo es obligatorio.',
      email: 'El correo no es válido.',
    },
    password: {
      required: 'La contraseña es obligatoria.',
      minlength: 'La contraseña debe tener al menos 6 caracteres.',
      maxlength: 'La contraseña no puede superar 50 caracteres.',
    },
  };

  constructor() {
    if (this.isEdit) {
      this.prepararEdicion();
    }

    // Guardar: mismo patrón de los demás formularios. Un único subscribe y la petición con switchMap
    this.veterinarioForm.events
      .pipe(
        filter((e) => e instanceof FormSubmittedEvent),
        filter(() => {
          this.veterinarioForm.markAllAsTouched();
          return this.veterinarioForm.valid;
        }),
        switchMap(() => {
          this.cargando = true;
          this.errorMensaje = '';
          const veterinario = this.armarRequest();
          const peticion$ = this.isEdit
            ? this.veterinarioService.updateVeterinario(this.cedula!, veterinario)
            : this.veterinarioService.createVeterinario(veterinario);
          // catchError DENTRO: si la API responde 409/400 el formulario sigue funcionando
          return peticion$.pipe(catchError((error) => this.manejarError(error, 'No se pudo guardar el veterinario.')));
        }),
        takeUntilDestroyed(),
      )
      .subscribe((veterinario) => {
        const mensaje = this.isEdit
          ? `Se actualizaron los datos de ${veterinario.nombre}.`
          : `Se registró a ${veterinario.nombre}. Ya puede ingresar con ${veterinario.correo}.`;
        this.router.navigate(['/admin/veterinarios'], { state: { mensaje } });
      });
  }

  // En edición la cédula no se cambia y la contraseña es opcional (vacía = se conserva)
  private prepararEdicion() {
    this.veterinarioForm.controls.cedula.disable();
    this.veterinarioForm.controls.password.setValidators([Validators.minLength(6), Validators.maxLength(50)]);
    this.veterinarioForm.controls.password.updateValueAndValidity();

    this.cargando = true;
    this.veterinarioService
      .getVeterinarioByCedula(this.cedula!)
      .pipe(
        catchError((error: HttpErrorResponse) => {
          if (error.status === 404) {
            this.noEncontrado = true;
            this.cargando = false;
            return EMPTY;
          }
          return this.manejarError(error, 'No se pudo cargar el veterinario.');
        }),
        takeUntilDestroyed(),
      )
      .subscribe((veterinario) => {
        this.veterinarioForm.patchValue({
          cedula: veterinario.cedula,
          nombre: veterinario.nombre,
          especialidad: veterinario.especialidad ?? '',
          foto: veterinario.foto ?? '',
          correo: veterinario.correo ?? '',
        });
        this.cargando = false;
      });
  }

  private armarRequest(): VeterinarioRequest {
    // getRawValue incluye la cédula aunque esté deshabilitada
    const valores = this.veterinarioForm.getRawValue();
    return {
      cedula: valores.cedula!.trim(),
      nombre: valores.nombre!.trim(),
      especialidad: valores.especialidad!.trim(),
      foto: valores.foto?.trim() || undefined,
      correo: valores.correo!.trim().toLowerCase(),
      password: valores.password || undefined,
    };
  }

  private manejarError(error: HttpErrorResponse, porDefecto: string) {
    this.cargando = false;
    this.errorMensaje = mensajeError(error, porDefecto);
    return EMPTY;
  }
}
