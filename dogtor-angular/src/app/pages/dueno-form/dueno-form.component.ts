import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Dueno } from '../../models/dueno.model';
import { DuenoService } from '../../service/dueno.service';
import { CampoTextoComponent } from '../../components/campo-texto/campo-texto.component';

@Component({
  selector: 'app-dueno-form',
  imports: [ReactiveFormsModule, RouterLink, CampoTextoComponent],
  templateUrl: './dueno-form.component.html',
  styleUrl: './dueno-form.component.scss',
})
export class DuenoFormComponent {
  //DI
  duenoService = inject(DuenoService);
  router = inject(Router);
  activatedRoute = inject(ActivatedRoute);

  duenoId: number | undefined = undefined;
  isEdit = false;
  noEncontrado = false;

  duenoForm = new FormGroup({
    nombre: new FormControl('', [
      Validators.required,
      Validators.minLength(3),
      Validators.maxLength(80),
      Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]+$/),
    ]),
    // Solo dígitos: 7 (fijo) a 10 (celular)
    telefono: new FormControl('', [Validators.required, Validators.pattern(/^\d{7,10}$/)]),
    direccion: new FormControl('', [Validators.maxLength(100)]),
  });

  // Texto de cada error por campo (clave = nombre del validador); lo pinta app-campo-error
  readonly mensajes = {
    nombre: {
      required: 'El nombre es obligatorio.',
      minlength: 'El nombre debe tener al menos 3 caracteres.',
      maxlength: 'El nombre no puede superar 80 caracteres.',
      pattern: 'El nombre solo puede contener letras.',
    },
    telefono: {
      required: 'El teléfono es obligatorio.',
      pattern: 'El teléfono debe tener entre 7 y 10 dígitos, sin espacios.',
    },
    direccion: { maxlength: 'La dirección no puede superar 100 caracteres.' },
  };

  ngOnInit() {
    const idParam = this.activatedRoute.snapshot.params['id'];
    if (idParam) {
      this.isEdit = true;
      this.duenoId = Number(idParam);

      const dueno = this.duenoService.getDuenoById(this.duenoId);
      if (dueno) {
        this.duenoForm.patchValue({
          nombre: dueno.nombre,
          telefono: dueno.telefono ?? '',
          direccion: dueno.direccion ?? '',
        });
      } else {
        this.noEncontrado = true;
      }
    }
  }

  handleSubmit() {
    if (this.duenoForm.invalid) {
      this.duenoForm.markAllAsTouched();
      return;
    }

    const formValue = this.duenoForm.value;

    const dueno: Dueno = {
      id: 0,
      nombre: formValue.nombre!.trim(),
      telefono: formValue.telefono!.trim(),
      direccion: formValue.direccion?.trim() || undefined,
    };

    let mensaje: string;
    if (this.isEdit) {
      this.duenoService.updateDueno(this.duenoId!, dueno);
      mensaje = `Se actualizaron los datos de ${dueno.nombre}.`;
    } else {
      this.duenoService.addDueno(dueno);
      mensaje = `Se registró a ${dueno.nombre}.`;
    }

    // El mensaje viaja en el state de la navegación y lo muestra la tabla
    this.router.navigate(['/duenos'], { state: { mensaje } });
  }
}
