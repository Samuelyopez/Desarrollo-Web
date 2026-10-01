import { Component, inject } from '@angular/core';
import { HeroCarouselComponent } from './components/hero-carousel/hero-carousel.component';
import { LandingService } from '../../service/landing.service';
import { Slide } from '../../models/landing.model';

@Component({
  selector: 'app-landing',
  imports: [HeroCarouselComponent],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.scss',
})
export class LandingComponent {
  //DI
  private landingService = inject(LandingService);

  // Los datos viven en LandingService, igual que en las demás páginas
  slides: Slide[] = this.landingService.getSlides();
}
