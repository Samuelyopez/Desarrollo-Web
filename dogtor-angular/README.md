# DogTor 🐾 - Frontend en Angular

Migración del frontend de DogTor (antes Spring Boot + Thymeleaf) a **Angular 19**, siguiendo la misma
estructura del curso: componentes standalone, servicios con inyección de dependencias (`inject()`),
entradas/salidas (`input()` / `output()`), control de flujo (`@if`, `@for`) y formularios reactivos.
Los estilos usan **Bootstrap 5.3** + **Bootstrap Icons** (cargados en `angular.json`) y SCSS.

> Por ahora no hay backend: los datos están "quemados" en los servicios (`src/app/service`).
> Como los servicios son singleton, los cambios se mantienen mientras navegas, pero al recargar (F5)
> vuelven los datos originales.

## Ejecutar

```bash
npm install
npm start
```

Luego abre `http://localhost:4200`.

## Estructura

```
src/app/
├── models/        Interfaces de las entidades de Spring Boot (Usuario, Administrador,
│                  Veterinario, Dueno, Mascota, RegistroMedico, Droga)
├── service/       Servicios con la "base de datos" quemada
├── components/    Navbar y footer compartidos
└── pages/
    ├── landing/              Página de inicio (carrusel)
    ├── mascota-table-page/   Listado de mascotas activas/inactivas
    ├── mascota-form/         Crear / editar mascota
    └── mascota-detail/       Detalle + historial médico
```

## Rutas

| Ruta                  | Página                   |
| --------------------- | ------------------------ |
| `/`                   | Landing                  |
| `/mascotas`           | Listado de mascotas      |
| `/mascota/new`        | Crear mascota            |
| `/mascota/update/:id` | Editar mascota           |
| `/mascota/:id`        | Detalle de mascota       |
