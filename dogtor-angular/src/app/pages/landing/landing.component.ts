import { Component, inject } from '@angular/core';
import { HeroCarouselComponent } from './components/hero-carousel/hero-carousel.component';
import { SectionHeaderComponent } from './components/section-header/section-header.component';
import { ServiceCardComponent } from './components/service-card/service-card.component';
import { PlanCardComponent } from './components/plan-card/plan-card.component';
import { TeamCardComponent } from './components/team-card/team-card.component';
import { TestimonioCardComponent } from './components/testimonio-card/testimonio-card.component';
import { CtaBannerComponent } from './components/cta-banner/cta-banner.component';
import { LandingService } from '../../service/landing.service';
import { MiembroEquipo, Plan, Servicio, Slide, Testimonio } from '../../models/landing.model';

@Component({
  selector: 'app-landing',
  imports: [
    HeroCarouselComponent,
    SectionHeaderComponent,
    ServiceCardComponent,
    PlanCardComponent,
    TeamCardComponent,
    TestimonioCardComponent,
    CtaBannerComponent,
  ],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.scss',
})
export class LandingComponent {
  //DI
  private landingService = inject(LandingService);

  // Los datos viven en LandingService, igual que en las demás páginas
  slides: Slide[] = this.landingService.getSlides();
  servicios: Servicio[] = this.landingService.getServicios();
  planes: Plan[] = this.landingService.getPlanes();
  equipo: MiembroEquipo[] = this.landingService.getEquipo();
  testimonios: Testimonio[] = this.landingService.getTestimonios();

  // Plan elegido en una plan-card; el formulario de contacto lo usa para precargar el mensaje
  planSeleccionado: Plan | undefined;

  elegirPlan(plan: Plan) {
    this.planSeleccionado = plan;
    // Lleva al usuario hasta la sección de contacto (el encabezado con ancla "contacto")
    document.getElementById('contacto')?.scrollIntoView({ behavior: 'smooth' });
  }
}
