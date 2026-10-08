import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Mascota } from '../../models/mascota.model';
import { MascotaService } from '../../service/mascota.service';
import { MascotaInfoCardComponent } from './components/mascota-info-card/mascota-info-card.component';

@Component({
  selector: 'app-mascota-detail',
  imports: [RouterLink, MascotaInfoCardComponent],
  templateUrl: './mascota-detail.component.html',
  styleUrl: './mascota-detail.component.scss',
})
export class MascotaDetailComponent {
  //DI
  route = inject(ActivatedRoute);
  mascotaService = inject(MascotaService);

  mascotaId = -1;
  mascota: Mascota | undefined;

  ngOnInit() {
    // 1. Obtener el id de la URL  2. Buscar la mascota
    this.mascotaId = Number(this.route.snapshot.params['id']);
    this.mascota = this.mascotaService.getMascotaById(this.mascotaId);
  }
}
