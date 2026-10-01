// Modelos de la landing. No son entidades de Spring Boot: son el contenido de la página de inicio

export interface Slide {
  img: string;
  title: string;
  desc: string;
  link?: string;
  btn?: string;
}

export interface Servicio {
  icono: string; // clase de bootstrap-icons, ej. "bi-heart-pulse"
  titulo: string;
  descripcion: string;
}

export interface Plan {
  nombre: string;
  precio: string; // ya formateado en pesos colombianos, ej. "$49.900"
  periodo: string;
  beneficios: string[];
  destacado: boolean;
}

export interface MiembroEquipo {
  nombre: string;
  cargo: string;
  descripcion: string;
}

export interface Testimonio {
  autor: string;
  mascota: string;
  texto: string;
  estrellas: number; // de 1 a 5
}

export interface DatosContacto {
  direccion: string;
  telefono: string;
  correo: string;
  horario: string;
}
