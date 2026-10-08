import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Mascota } from '../../models/mascota.model';
import { Dueno } from '../../models/dueno.model';
import { MascotaService } from '../../service/mascota.service';
import { DuenoService } from '../../service/dueno.service';
import { CampoTextoComponent } from '../../components/campo-texto/campo-texto.component';
import { DuenoSelectComponent } from './components/dueno-select/dueno-select.component';
import { FotoPreviewComponent } from './components/foto-preview/foto-preview.component';

@Component({
  selector: 'app-mascota-form',
  imports: [ReactiveFormsModule, RouterLink, CampoTextoComponent, DuenoSelectComponent, FotoPreviewComponent],
  templateUrl: './mascota-form.component.html',
  styleUrl: './mascota-form.component.scss',
})
export class MascotaFormComponent {
  //DI
  mascotaService = inject(MascotaService);
  duenoService = inject(DuenoService);
  router = inject(Router);
  activatedRoute = inject(ActivatedRoute);

  mascotaId: number | undefined = undefined;
  isEdit = false;
  noEncontrada = false;
  duenos: Dueno[] = [];

  // Se conserva el estado al editar para no reactivar una mascota inactiva
  private activaActual = true;

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
    // El control guarda el objeto Dueno completo, no su id
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

  ngOnInit() {
    this.duenos = this.duenoService.getDuenos();

    const idParam = this.activatedRoute.snapshot.params['id'];
    if (idParam) {
      this.isEdit = true;
      this.mascotaId = Number(idParam);

      const mascota = this.mascotaService.getMascotaById(this.mascotaId);
      if (mascota) {
        this.activaActual = mascota.activa;
        this.mascotaForm.patchValue({
          nombre: mascota.nombre,
          raza: mascota.raza ?? '',
          edad: mascota.edad?.toString() ?? '',
          peso: mascota.peso?.toString() ?? '',
          enfermedad: mascota.enfermedad ?? '',
          foto: mascota.foto ?? '',
          dueno: mascota.dueno ?? null,
        });
      } else {
        this.noEncontrada = true;
      }
    }
  }

  handleSubmit() {
    if (this.mascotaForm.invalid) {
      this.mascotaForm.markAllAsTouched();
      return;
    }

    const formValue = this.mascotaForm.value;

    const mascota: Mascota = {
      id: 0,
      nombre: formValue.nombre!.trim(),
      raza: formValue.raza?.trim() || undefined,
      edad: formValue.edad ? Number(formValue.edad) : undefined,
      peso: formValue.peso ? Number(formValue.peso.replace(',', '.')) : undefined,
      enfermedad: formValue.enfermedad?.trim() || undefined,
      foto: formValue.foto?.trim() || undefined,
      activa: this.isEdit ? this.activaActual : true,
      duenoId: formValue.dueno?.id,
      dueno: formValue.dueno ?? undefined,
    };

    if (this.isEdit) {
      this.mascotaService.updateMascota(this.mascotaId!, mascota);
    } else {
      this.mascotaService.addMascota(mascota);
    }

    this.router.navigate(['/vet/mascotas']);
  }
}
