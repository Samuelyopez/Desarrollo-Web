import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Slide } from '../../../../models/landing.model';

@Component({
  selector: 'app-hero-carousel',
  imports: [RouterLink],
  templateUrl: './hero-carousel.component.html',
  styleUrl: './hero-carousel.component.scss',
})
export class HeroCarouselComponent {
  // Entrada: las diapositivas vienen de LandingService a través del padre
  slides = input.required<Slide[]>();

  activeSlide = 0;

  anterior() {
    this.activeSlide = this.activeSlide === 0 ? this.slides().length - 1 : this.activeSlide - 1;
  }

  siguiente() {
    this.activeSlide = this.activeSlide === this.slides().length - 1 ? 0 : this.activeSlide + 1;
  }
}
