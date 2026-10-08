import { Injectable } from '@angular/core';
import { DatosContacto, MiembroEquipo, Plan, Servicio, Slide, Testimonio } from '../models/landing.model';

@Injectable({
  providedIn: 'root',
})
export class LandingService {
  // Datos de la landing, igual que los demás servicios: arreglos quemados porque no hay backend
  private slides: Slide[] = [
    {
      img: '/img/landing/slide-mascotas.jpg',
      title: 'Cuidamos a tus Pacientes',
      desc: 'Consulta el historial médico de tu mascota en un solo lugar.',
      link: '/login',
      btn: 'Ingresar',
    },
    {
      img: '/img/landing/slide-equipo.jpg',
      title: 'Nuestro Equipo',
      desc: 'Conoce a los especialistas de DogTor.',
    },
  ];

  private servicios: Servicio[] = [
    { icono: 'bi-heart-pulse', titulo: 'Consulta general', descripcion: 'Revisión completa del estado de salud de tu mascota con nuestros veterinarios.' },
    { icono: 'bi-shield-check', titulo: 'Vacunación', descripcion: 'Esquemas de vacunación al día para perros y gatos de todas las edades.' },
    { icono: 'bi-scissors', titulo: 'Cirugía', descripcion: 'Procedimientos quirúrgicos con anestesia monitoreada y control posoperatorio.' },
    { icono: 'bi-capsule', titulo: 'Farmacia', descripcion: 'Medicamentos veterinarios formulados por nuestros especialistas.' },
    { icono: 'bi-house-heart', titulo: 'Hospitalización', descripcion: 'Cuidado las 24 horas para los pacientes que necesitan observación.' },
    { icono: 'bi-droplet', titulo: 'Peluquería y baño', descripcion: 'Baño, corte y limpieza para que tu mascota luzca y se sienta bien.' },
  ];

  private planes: Plan[] = [
    {
      nombre: 'Básico',
      precio: '$49.900',
      periodo: '/mes',
      beneficios: ['1 consulta general al mes', 'Carné de vacunación digital', '10% de descuento en farmacia'],
      destacado: false,
    },
    {
      nombre: 'Plus',
      precio: '$89.900',
      periodo: '/mes',
      beneficios: [
        '2 consultas generales al mes',
        'Vacunación anual incluida',
        'Desparasitación cada 3 meses',
        '15% de descuento en farmacia',
      ],
      destacado: true,
    },
    {
      nombre: 'Premium',
      precio: '$149.900',
      periodo: '/mes',
      beneficios: [
        'Consultas generales ilimitadas',
        'Vacunación y desparasitación incluidas',
        'Baño y peluquería una vez al mes',
        '20% de descuento en cirugías',
        'Atención prioritaria',
      ],
      destacado: false,
    },
  ];

  // Los dos veterinarios son los mismos usuarios VETERINARIO de UsuarioService
  private equipo: MiembroEquipo[] = [
    { nombre: 'Dr. Andres Felipe', cargo: 'Médico veterinario', descripcion: 'Medicina general y control de pacientes caninos y felinos.' },
    { nombre: 'Dra. Laura Jimenez', cargo: 'Médica veterinaria', descripcion: 'Dermatología y tratamiento de alergias en mascotas.' },
    { nombre: 'Paola Méndez', cargo: 'Recepción y atención al cliente', descripcion: 'Agenda tus citas y resuelve tus dudas sobre los planes.' },
  ];

  private testimonios: Testimonio[] = [
    { autor: 'Juan Pérez', mascota: 'Max', texto: 'Atendieron a Max muy rápido y nos explicaron todo el tratamiento.', estrellas: 5 },
    { autor: 'María López', mascota: 'Rocky', texto: 'El plan Plus nos ha ahorrado mucho en vacunas y consultas.', estrellas: 5 },
    { autor: 'Sofía Martínez', mascota: 'Thor', texto: 'Muy buena atención; a veces hay que esperar un poco en recepción.', estrellas: 4 },
  ];

  private contacto: DatosContacto = {
    direccion: 'Calle Falsa 123, Bogotá',
    telefono: '+57 123 456 7890',
    correo: 'contacto@dogtor.com',
    horario: 'Lunes a sábado, 8:00 a. m. a 6:00 p. m.',
  };

  getSlides() {
    return this.slides;
  }

  getServicios() {
    return this.servicios;
  }

  getPlanes() {
    return this.planes;
  }

  getEquipo() {
    return this.equipo;
  }

  getTestimonios() {
    return this.testimonios;
  }

  getContacto() {
    return this.contacto;
  }
}
