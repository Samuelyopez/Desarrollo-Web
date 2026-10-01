import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Mascota } from '../../models/mascota.model';
import { RegistroMedico } from '../../models/registro-medico.model';
import { MascotaService } from '../../service/mascota.service';
import { RegistroMedicoService } from '../../service/registro-medico.service';
import { DrogaService } from '../../service/droga.service';
import { UsuarioService } from '../../service/usuario.service';
import { MascotaInfoCardComponent } from './components/mascota-info-card/mascota-info-card.component';
import { RegistroMedicoItemComponent } from './components/registro-medico-item/registro-medico-item.component';

// Registro médico con los nombres ya resueltos, listo para pintarse
interface RegistroVista {
  registro: RegistroMedico;
  veterinario: string;
  drogas: string[];
}

@Component({
  selector: 'app-mascota-detail',
  imports: [RouterLink, MascotaInfoCardComponent, RegistroMedicoItemComponent],
  templateUrl: './mascota-detail.component.html',
  styleUrl: './mascota-detail.component.scss',
})
export class MascotaDetailComponent {
  //DI
  route = inject(ActivatedRoute);
  mascotaService = inject(MascotaService);
  registroMedicoService = inject(RegistroMedicoService);
  drogaService = inject(DrogaService);
  usuarioService = inject(UsuarioService);

  mascotaId = -1;
  mascota: Mascota | undefined;

  // Se arma UNA vez en ngOnInit. Si el template llamara a un método que devuelve un arreglo
  // nuevo, cada detección de cambios vería un valor distinto (error NG0100 en desarrollo)
  registros: RegistroVista[] = [];

  ngOnInit() {
    // 1. Obtener el id de la URL  2. Buscar la mascota  3. Preparar su historial
    this.mascotaId = Number(this.route.snapshot.params['id']);
    this.mascota = this.mascotaService.getMascotaById(this.mascotaId);

    if (this.mascota) {
      this.registros = this.registroMedicoService.getRegistrosByMascota(this.mascotaId).map((registro) => ({
        registro,
        veterinario: this.getNombreVeterinario(registro.veterinarioId),
        drogas: registro.drogaIds
          .map((id) => this.drogaService.getDrogaById(id)?.nombre)
          .filter((nombre): nombre is string => !!nombre),
      }));
    }
  }

  private getNombreVeterinario(veterinarioId?: number) {
    if (veterinarioId === undefined) {
      return 'Sin asignar';
    }
    return this.usuarioService.getUsuarioById(veterinarioId)?.nombre ?? 'Sin asignar';
  }
}
