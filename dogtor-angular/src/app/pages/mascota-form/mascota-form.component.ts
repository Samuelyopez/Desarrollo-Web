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
    edad: new FormControl('', [Validators.maxLength(30)]),
    fotoUrl: new FormControl('', [Validators.pattern(/^https?:\/\/.+/)]),
    vacunas: new FormControl('', [Validators.maxLength(100)]),
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
    edad: { maxlength: 'La edad no puede superar 30 caracteres.' },
    fotoUrl: { pattern: 'La URL debe empezar por http:// o https://' },
    vacunas: { maxlength: 'Las vacunas no pueden superar 100 caracteres.' },
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
          edad: mascota.edad ?? '',
          fotoUrl: mascota.fotoUrl ?? '',
          vacunas: mascota.vacunas ?? '',
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
      edad: formValue.edad?.trim() || undefined,
      fotoUrl: formValue.fotoUrl?.trim() || undefined,
      vacunas: formValue.vacunas?.trim() || undefined,
      activa: this.isEdit ? this.activaActual : true,
      dueno: formValue.dueno ?? undefined,
    };

    if (this.isEdit) {
      this.mascotaService.updateMascota(this.mascotaId!, mascota);
    } else {
      this.mascotaService.addMascota(mascota);
    }

    this.router.navigate(['/mascotas']);
  }
}
