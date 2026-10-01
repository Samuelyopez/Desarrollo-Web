import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

interface Slide {
  img: string;
  title: string;
  desc: string;
  link?: string;
  btn?: string;
}

@Component({
  selector: 'app-landing',
  imports: [RouterLink],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.scss',
})
export class LandingComponent {
  activeSlide = 0;

  slides: Slide[] = [
    {
      img: 'https://images.unsplash.com/photo-1552053831-71594a27632d?w=1200',
      title: 'Cuidamos a tus Pacientes',
      desc: 'Consulta el historial médico de tu mascota en un solo lugar.',
      link: '/mascotas',
      btn: 'Ver Mascotas',
    },
    {
      img: 'https://images.unsplash.com/photo-1537151608804-ea6f117f73d2?w=1200',
      title: 'Nuestro Equipo',
      desc: 'Conoce a los especialistas de DogTor.',
    },
  ];

  anterior() {
    this.activeSlide = this.activeSlide === 0 ? this.slides.length - 1 : this.activeSlide - 1;
  }

  siguiente() {
    this.activeSlide = this.activeSlide === this.slides.length - 1 ? 0 : this.activeSlide + 1;
  }
}
