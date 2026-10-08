import { Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { FormControl, FormGroup, FormSubmittedEvent, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { catchError, EMPTY, filter, switchMap } from 'rxjs';
import { AuthService } from '../../service/auth.service';
import { mensajeError } from '../../utils/mensaje-error';
import { CampoTextoComponent } from '../../components/campo-texto/campo-texto.component';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, CampoTextoComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  //DI
  private auth = inject(AuthService);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);

  loginForm = new FormGroup({
    correo: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required]),
  });

  readonly mensajes = {
    correo: { required: 'El correo es obligatorio.', email: 'El correo no es válido.' },
    password: { required: 'La contraseña es obligatoria.' },
  };

  cargando = false;
  errorMensaje = '';

  constructor() {
    // Reaccionamos a los eventos del formulario (RXJS), igual que en el taller:
    // un único subscribe y la petición encadenada con switchMap
    this.loginForm.events
      .pipe(
        // 1. Solo el evento de submit y cuando el formulario es válido
        filter((e) => e instanceof FormSubmittedEvent),
        filter(() => {
          this.loginForm.markAllAsTouched();
          return this.loginForm.valid;
        }),

        // 2. Pedimos el login a la API. switchMap cancela un intento anterior que siga en curso.
        //    El catchError va DENTRO para que un error no cierre el flujo y se pueda reintentar
        switchMap(() => {
          this.cargando = true;
          this.errorMensaje = '';
          const { correo, password } = this.loginForm.getRawValue();

          return this.auth
            .login({ correo: correo!.trim(), password: password! })
            .pipe(catchError((error: HttpErrorResponse) => this.manejarError(error)));
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((usuario) => {
        this.cargando = false;
        this.router.navigateByUrl(this.auth.rutaInicio(usuario.rol));
      });
  }

  private manejarError(error: HttpErrorResponse) {
    this.cargando = false;
    this.errorMensaje = mensajeError(error, 'No se pudo iniciar sesión.');
    return EMPTY;
  }
}
