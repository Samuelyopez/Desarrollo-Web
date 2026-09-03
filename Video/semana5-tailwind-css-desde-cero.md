# Semana 5: Framework Estilizado con Tailwind CSS

## 1. Resumen Ejecutivo y Alcance
Este módulo cubre el diseño de interfaces web modernas y responsive utilizando Tailwind CSS, un framework CSS de bajo nivel orientado a clases utilitarias ("Utility-First"). Define las reglas para maquetar sin salir del HTML.

## 2. Conceptos Técnicos Clave
- **Enfoque Utility-First**: En lugar de componentes prefabricados, Tailwind proporciona clases atómicas para propiedades CSS individuales (`p-4`, `flex`, `text-center`, `bg-blue-500`).
- **Clases Utilitarias Principales**:
  - Tipografía & Colores: `text-xl`, `font-bold`, `text-gray-700`, `bg-indigo-600`, `text-white`.
  - Layout & Flexbox/Grid: `flex`, `flex-col`, `items-center`, `justify-between`, `grid`, `grid-cols-1`, `md:grid-cols-2`, `gap-6`.
  - Espaciado & Dimensiones: `p-6`, `px-4`, `py-2`, `m-4`, `mt-2`, `w-full`, `w-1/2`, `h-64`, `max-w-md`, `mx-auto`.
  - Bordes & Sombras: `rounded-lg`, `rounded-full`, `shadow-md`, `border`, `border-gray-300`.
  - Pseudo-clases y Estados: `hover:bg-indigo-700`, `focus:outline-none`, `focus:ring-2`.
- **Responsive Design (Mobile-First)**:
  - Sin prefijo: Aplica a todos los tamaños (móvil por defecto).
  - Prefijos de Breakpoints: `sm:` (>=640px), `md:` (>=768px), `lg:` (>=1024px), `xl:` (>=1280px).

## 3. Patrones de Código y Arquitectura
### Tarjeta y Rejilla Responsive con Tailwind CSS
```html
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Portafolio Tailwind</title>
  <!-- CDN de Play Tailwind para desarrollo -->
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-900 text-white min-h-screen p-8">

  <header class="max-w-4xl mx-auto text-center mb-12">
    <h1 class="text-4xl font-extrabold text-blue-400 mb-2">Desarrollador Full Stack</h1>
    <p class="text-slate-400 text-lg">Especializado en Spring Boot, Thymeleaf y Tailwind CSS</p>
  </header>

  <main class="max-w-4xl mx-auto grid grid-cols-1 md:grid-cols-2 gap-8">
    
    <article class="bg-slate-800 p-6 rounded-xl shadow-lg hover:border-blue-500 border border-transparent transition">
      <h2 class="text-2xl font-bold mb-2 text-blue-300">Proyecto Backend</h2>
      <p class="text-slate-300 mb-4">Sistema CRUD completo desarrollado con Java y JPA.</p>
      <a href="#" class="inline-block bg-blue-600 hover:bg-blue-700 text-white font-medium px-4 py-2 rounded-lg transition">
        Ver Proyecto
      </a>
    </article>

  </main>
</body>
</html>
```

## 4. Buenas Prácticas Requeridas
1. **Diseño Mobile-First**: Definir primero los estilos base para dispositivos móviles y agregar prefijos (`md:`, `lg:`) para ajustar en pantallas más grandes.
2. **Consistencia en Paleta de Colores**: Utilizar la escala de intensidades nativa de Tailwind (`100` a `900`) para mantener coherencia visual.
3. **Contenedores Centrados**: Utilizar `max-w-*` junto con `mx-auto` para limitar el ancho y centrar el contenido principal.

## 5. Restricciones y Reglas de Desarrollo
- **PROHIBIDO** mezclar archivos CSS tradicionales extensos con estilos en línea cuando se esté utilizando Tailwind CSS.
- **NO DECLARAR** valores estáticos en píxeles arbitrarios en código de producción cuando exista la clase utilitaria correspondiente en la escala de Tailwind.
- **NO OLVIDAR** que la versión Play CDN (`script src="https://cdn.tailwindcss.com"`) es exclusivamente para pruebas/desarrollo y no debe usarse en entornos de producción intensiva sin compilación PostCSS.