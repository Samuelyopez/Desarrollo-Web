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
  };

  ngOnInit() {
    const idParam = this.activatedRoute.snapshot.params['id'];
    if (idParam) {
      this.isEdit = true;
      this.duenoId = Number(idParam);

      const dueno = this.duenoService.getDuenoById(this.duenoId);
      if (dueno) {
        this.duenoForm.patchValue({
          cedula: dueno.cedula,
          nombre: dueno.nombre,
          celular: dueno.celular ?? '',
          correo: dueno.correo ?? '',
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
      cedula: formValue.cedula!.trim(),
      nombre: formValue.nombre!.trim(),
      celular: formValue.celular!.trim(),
      correo: formValue.correo!.trim().toLowerCase(),
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
    this.router.navigate(['/vet/duenos'], { state: { mensaje } });
  }
}
