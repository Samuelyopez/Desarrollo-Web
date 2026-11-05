# Taller RXJS — Buscador de perfiles

**Autor:** Samuel Lopez Gomez · Desarrollo Web · Pontificia Universidad Javeriana

Aplicación Angular 19 (sin routing) que busca un usuario por su `username` en [dummyjson.com](https://dummyjson.com) y muestra sus datos, sus posts, las reacciones de cada post y los comentarios con el nombre de quien comentó.

## Cómo ejecutarlo

```bash
npm install
ng serve
```

Abrir http://localhost:4200 y buscar, por ejemplo: `emilys`, `stellas` (6 posts), `averya`, `victoriam`.

> Nota: el usuario `atuny0` del enunciado ya no existe en la API actual de dummyjson; la app muestra el aviso de "usuario no existe".

## Estructura

```
src/app/
├── model/                 Interfaces de las entidades (user, post, comment)
├── service/               Peticiones HTTP, un servicio por entidad
│   ├── user.service.ts      searchUser(username)
│   ├── post.service.ts      getPostsByUser(userId)
│   └── comment.service.ts   getCommentsByPost(postId)
├── pages/user-search/     Componente principal: barra de búsqueda + consultas
└── components/
    ├── user-info/         Sección 2: datos del usuario   (@Input user)
    └── user-posts/        Sección 3: posts + comentarios (@Input posts)
```

## Buenas prácticas aplicadas (según la clase)

- `provideHttpClient()` en `app.config.ts` e `inject(HttpClient)` en los servicios.
- Las peticiones HTTP viven en los **servicios**; el componente solo maneja la pantalla.
- Peticiones tipadas con interfaces (`get<UsersResponse>`, etc.) y servicios que devuelven `Observable<T>`.
- Formulario reactivo (`FormGroup`) y reacción a `userForm.events` filtrando `FormSubmittedEvent`.
- **Un solo `subscribe`**, sin subscribes anidados: usuario → posts → comentarios se encadenan con `switchMap`.
- `switchMap` cancela la búsqueda anterior si el usuario busca otro rápido, así nunca se mezclan datos de dos usuarios.
- `forkJoin` trae en paralelo los comentarios de todos los posts y emite cuando terminan todos.
- El componente principal hace las consultas y pasa la información a los hijos con `@Input`.
- Bootstrap + Bootstrap Icons para la interfaz y el número de reacciones.
