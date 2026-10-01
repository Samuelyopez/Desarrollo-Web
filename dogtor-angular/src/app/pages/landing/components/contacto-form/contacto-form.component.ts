import { Component, effect, input, output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { DatosContacto, Plan } from '../../../../models/landing.model';
import { CampoTextoComponent } from '../../../../components/campo-texto/campo-texto.component';
import { CampoErrorComponent } from '../../../../components/campo-error/campo-error.component';

@Component({
  selector: 'app-contacto-form',
  imports: [ReactiveFormsModule, CampoTextoComponent, CampoErrorComponent],
  templateUrl: './contacto-form.component.html',
  styleUrl: './contacto-form.component.scss',
})
export class ContactoFormComponent {
  // Entradas: datos de la clínica y el plan elegido en la sección de planes (si hay uno)
  datos = input.required<DatosContacto>();
  plan = input<Plan | undefined>();

  // Salida: avisa al padre que se envió el formulario. No se manda a ningún servidor (no hay backend)
  enviado = output<{ nombre: string; correo: string; mensaje: string }>();

  enviadoConExito = false;

  contactoForm = new FormGroup({
    nombre: new FormControl('', [Validators.required, Validators.minLength(2)]),
    correo: new FormControl('', [Validators.required, Validators.email]),
    mensaje: new FormControl('', [Validators.required, Validators.minLength(10)]),
  });

  // Texto de cada error por campo (clave = nombre del validador); lo pinta app-campo-error
  readonly mensajes = {
    nombre: { required: 'El nombre es obligatorio.', minlength: 'El nombre debe tener al menos 2 caracteres.' },
    correo: { required: 'El correo es obligatorio.', email: 'Escribe un correo válido.' },
    mensaje: { required: 'El mensaje es obligatorio.', minlength: 'El mensaje debe tener al menos 10 caracteres.' },
  };

  constructor() {
    // Cada vez que llega un plan nuevo desde la landing, se precarga el mensaje
    effect(() => {
      const plan = this.plan();
      if (plan) {
        this.contactoForm.controls.mensaje.setValue(`Me interesa el plan ${plan.nombre}.`);
        this.enviadoConExito = false;
      }
    });
  }

  handleSubmit() {
    if (this.contactoForm.invalid) {
      this.contactoForm.markAllAsTouched();
      return;
    }

    const valor = this.contactoForm.getRawValue();
    this.enviado.emit({
      nombre: (valor.nombre ?? '').trim(),
      correo: (valor.correo ?? '').trim(),
      mensaje: (valor.mensaje ?? '').trim(),
    });
    this.contactoForm.reset();
    this.enviadoConExito = true;
  }
}
