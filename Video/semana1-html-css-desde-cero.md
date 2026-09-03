# Semana 1: HTML5 y CSS3 desde Cero

## 1. Resumen Ejecutivo y Alcance
Este módulo establece los fundamentos para el desarrollo web cliente. Cubre la maquetación semántica estructurada en HTML5 y la estilización limpia mediante CSS3. Está diseñado para servir como la base estructural sobre la cual se construirán interfaces web modernas, accesibles y adaptables.

## 2. Conceptos Técnicos Clave
- **HTML5 Semántico**: Uso de etiquetas con significado estructural (`<header>`, `<main>`, `<footer>`, `<section>`, `<article>`, `<nav>`) para mejorar accesibilidad, SEO y legibilidad del código.
- **Modelo de Caja (Box Model)**: Entendimiento de `content`, `padding`, `border` y `margin`. Configuración clave `box-sizing: border-box`.
- **Layout con Flexbox**:
  - Contenedor padre: `display: flex`, `flex-direction` (`row` | `column`), `justify-content` (`center` | `space-between` | `space-around`), `align-items` (`center` | `stretch`).
  - Hijos flexibles: `flex-grow`, `flex-shrink`, `flex-basis`.
- **Selectores CSS3**:
  - Básicos: Etiqueta (`h1`), Clase (`.card`), ID (`#main-header`).
  - Avanzados: Descendientes (`ul li`), Hijos directos (`header > h2`), Combinadores (`h1, h3`), Pseudo-clases (`:hover`, `:nth-child(even)`).
- **Responsive Design**: Uso de `@media (min-width: ...)` para adaptar la interfaz a dispositivos móviles, tablets y escritorio.

## 3. Patrones de Código y Arquitectura
### Estructura HTML5 Semántica
```html
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>BogoRock - Emisora de Rock</title>
  <link rel="stylesheet" href="styles/styles.css">
</head>
<body>
  <header>
    <h1>BogoRock</h1>
    <nav>
      <ul>
        <li><a href="#artistas">Artistas</a></li>
        <li><a href="#contacto">Contacto</a></li>
      </ul>
    </nav>
  </header>
  <main>
    <section id="artistas">
      <h2>Artistas del Momento</h2>
      <article class="card">
        <img src="images/banda.jpg" alt="Fotografía de la banda Måneskin">
        <h3>Måneskin</h3>
        <p class="small">Banda italiana de rock alternativo.</p>
      </article>
    </section>
  </main>
  <footer>
    <p>&copy; 2026 BogoRock. Todos los derechos reservados.</p>
  </footer>
</body>
</html>
```

### Reglas CSS Universales y Flexbox
```css
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: Arial, sans-serif;
  line-height: 1.6;
}

.card-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 1rem;
}

.card {
  flex: 1 1 calc(33.333% - 1rem);
  border: 1px solid #ccc;
  padding: 1rem;
  border-radius: 8px;
}
```

## 4. Buenas Prácticas Requeridas
1. **Atributos ALT Obligatorios**: Todas las imágenes (`<img>`) deben incluir un atributo `alt` descriptivo para accesibilidad.
2. **Normalización del Modelo de Caja**: Aplicar `box-sizing: border-box` a todos los elementos mediante el selector universal `*`.
3. **Separación de Responsabilidades**: Todo el código de estilos debe residir en archivos `.css` externos vinculados mediante `<link>`.
4. **Un h1 por Página**: Cada documento HTML debe contener únicamente una etiqueta `<h1>` principal.

## 5. Restricciones y Reglas de Desarrollo
- **PROHIBIDO** el uso de estilos en línea (`style="..."`) salvo para prototipado rápido temporal.
- **NO USAR** estructuras `<div>` indiscriminadas ("divitis") cuando exista una etiqueta semántica adecuada (`<section>`, `<article>`, `<nav>`).
- **NO UTILIZAR** IDs para aplicar estilos en CSS; reservar los IDs para enlaces internos (`#contacto`) o manipulaciones específicas desde JavaScript.
- **SIEMPRE** especificar la codificación `<meta charset="UTF-8">` y el viewport en la etiqueta `<head>`.