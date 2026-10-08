import { Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { FormControl, FormGroup, FormSubmittedEvent, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { catchError, EMPTY, filter, switchMap } from 'rxjs';
import { DuenoRequest } from '../../models/dueno.model';
import { DuenoService } from '../../service/dueno.service';
import { CampoTextoComponent } from '../../components/campo-texto/campo-texto.component';
import { mensajeError } from '../../utils/mensaje-error';

@Component({
  selector: 'app-dueno-form',
  imports: [ReactiveFormsModule, RouterLink, CampoTextoComponent],
  templateUrl: './dueno-form.component.html',
  styleUrl: './dueno-form.component.scss',
})
export class DuenoFormComponent {
  //DI
  private duenoService = inject(DuenoService);
  private router = inject(Router);
  private activatedRoute = inject(ActivatedRoute);

  // La cédula de la URL identifica al dueño que se edita (no existe al crear)
  cedula: string | undefined = this.activatedRoute.snapshot.params['cedula'];
  isEdit = this.cedula !== undefined;
  noEncontrado = false;
  cargando = false;
  errorMensaje = '';

  duenoForm = new FormGroup({
    // Solo dígitos: 6 a 10
    cedula: new FormControl('', [Validators.required, Validators.pattern(/^\d{6,10}$/)]),
    nombre: new FormControl('', [
      Validators.required,
      Validators.minLength(3),
      Validators.maxLength(80),
      Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]+$/),
    ]),
    // Celular colombiano: 10 dígitos que empiezan por 3
    celular: new FormControl('', [Validators.required, Validators.pattern(/^3\d{9}$/)]),
    correo: new FormControl('', [Validators.required, Validators.email]),
    // Con la que el cliente entra a su portal. Obligatoria solo al crear
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
    celular: {
      required: 'El celular es obligatorio.',
      pattern: 'El celular debe tener 10 dígitos y empezar por 3.',
    },
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

    // Guardar: mismo patrón del login. Un único subscribe y la petición encadenada con switchMap
    this.duenoForm.events
      .pipe(
        filter((e) => e instanceof FormSubmittedEvent),
        filter(() => {
          this.duenoForm.markAllAsTouched();
          return this.duenoForm.valid;
        }),
        switchMap(() => {
          this.cargando = true;
          this.errorMensaje = '';
          const dueno = this.armarRequest();
          const peticion$ = this.isEdit
            ? this.duenoService.updateDueno(this.cedula!, dueno)
            : this.duenoService.createDueno(dueno);
          // catchError DENTRO: si la API responde 409/400 el formulario sigue funcionando
          return peticion$.pipe(catchError((error) => this.manejarError(error, 'No se pudo guardar el dueño.')));
        }),
        takeUntilDestroyed(),
      )
      .subscribe((dueno) => {
        const mensaje = this.isEdit ? `Se actualizaron los datos de ${dueno.nombre}.` : `Se registró a ${dueno.nombre}.`;
        // El mensaje viaja en el state de la navegación y lo muestra la tabla
        this.router.navigate(['/vet/duenos'], { state: { mensaje } });
      });
  }

  // En edición la cédula no se cambia y la contraseña es opcional (vacía = se conserva)
  private prepararEdicion() {
    this.duenoForm.controls.cedula.disable();
    this.duenoForm.controls.password.setValidators([Validators.minLength(6), Validators.maxLength(50)]);
    this.duenoForm.controls.password.updateValueAndValidity();

    this.cargando = true;
    this.duenoService
      .getDuenoByCedula(this.cedula!)
      .pipe(
        catchError((error: HttpErrorResponse) => {
          if (error.status === 404) {
            this.noEncontrado = true;
            this.cargando = false;
            return EMPTY;
          }
          return this.manejarError(error, 'No se pudo cargar el dueño.');
        }),
        takeUntilDestroyed(),
      )
      .subscribe((dueno) => {
        this.duenoForm.patchValue({
          cedula: dueno.cedula,
          nombre: dueno.nombre,
          celular: dueno.celular ?? '',
          correo: dueno.correo ?? '',
        });
        this.cargando = false;
      });
  }

  private armarRequest(): DuenoRequest {
    // getRawValue incluye la cédula aunque esté deshabilitada
    const valores = this.duenoForm.getRawValue();
    return {
      cedula: valores.cedula!.trim(),
      nombre: valores.nombre!.trim(),
      celular: valores.celular!.trim(),
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
