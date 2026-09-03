# Semana 3: Framework de Diseño Bootstrap 5

## 1. Resumen Ejecutivo y Alcance
Este módulo aborda el diseño de interfaces web responsive mediante el uso del framework utilitario y basado en componentes Bootstrap 5 (versión pura sin jQuery). Define las reglas para estructurar la rejilla de 12 columnas y utilizar sus componentes estandarizados.

## 2. Conceptos Técnicos Clave
- **Sistema de Cuadrícula (Grid System)**: Basado en Flexbox con una estructura jerárquica obligatoria:
  - `.container` o `.container-fluid` -> `.row` -> `.col-*`.
  - Estructura de 12 columnas con breakpoints: `col-sm-*` (>=576px), `col-md-*` (>=768px), `col-lg-*` (>=992px), `col-xl-*` (>=1200px).
- **Componentes Clave**:
  - Navbar: `.navbar`, `.navbar-expand-lg`, `.navbar-nav`, `.nav-link`.
  - Cards: `.card`, `.card-body`, `.card-title`, `.card-text`.
  - Formularios: `.form-control`, `.form-label`, `.form-select`, `.was-validated`.
  - Ventanas Modales: `.modal`, `.modal-dialog`, `.modal-content`, activados vía atributos `data-bs-toggle="modal"` y `data-bs-target="#myModal"`.
- **Clases Utilitarias**:
  - Espaciado: Márgenes (`m-1` a `m-5`, `mt-*`, `mb-*`, `mx-auto`) y Paddings (`p-1` a `p-5`, `px-*`, `py-*`).
  - Flexbox: `d-flex`, `justify-content-center`, `align-items-center`, `flex-column`.
  - Colores y Botones: `btn btn-primary`, `btn-outline-danger`, `bg-light`, `text-center`.

## 3. Patrones de Código y Arquitectura
### Estructura Responsive de Cuadrícula con Tarjetas
```html
<div class="container my-5">
  <h2 class="text-center mb-4">Productos Destacados</h2>
  <div class="row g-4">
    <!-- 12 cols en móvil, 6 en tablet (md), 4 en escritorio (lg) -->
    <div class="col-12 col-md-6 col-lg-4">
      <div class="card h-100 shadow-sm">
        <img src="images/joya1.jpg" class="card-img-top" alt="Anillo de esmeralda">
        <div class="card-body d-flex flex-column">
          <h5 class="card-title">Anillo Esmeralda</h5>
          <p class="card-text text-muted">Joya fina tallada en plata de ley 925.</p>
          <button class="btn btn-primary mt-auto" data-bs-toggle="modal" data-bs-target="#modalDetalle">Ver Detalle</button>
        </div>
      </div>
    </div>
  </div>
</div>
```

### Formulario Validado con Bootstrap 5
```html
<form class="needs-validation" novalidate>
  <div class="mb-3">
    <label for="email" class="form-label">Correo Electrónico</label>
    <input type="email" class="form-control" id="email" required>
    <div class="invalid-feedback">Por favor ingrese un correo válido.</div>
  </div>
  <button type="submit" class="btn btn-success">Enviar</button>
</form>
```

## 4. Buenas Prácticas Requeridas
1. **Respetar la Jerarquía del Grid**: Nunca colocar una columna `.col-*` directamente sin un padre `.row` ni un `.row` sin un `.container`.
2. **Carga Correcta del JavaScript de Bootstrap**: Incluir el script `bootstrap.bundle.min.js` (que incluye Popper) antes del cierre de `</body>`.
3. **Personalización Segura de Estilos**: Vincular la hoja de CSS personalizada *después* del CDN de Bootstrap para permitir sobreescritura limpia.

## 5. Restricciones y Reglas de Desarrollo
- **PROHIBIDO** incluir jQuery. Bootstrap 5 utiliza JavaScript Vanilla puro.
- **NO SOBREESCRIBIR** clases nativas de Bootstrap con la regla `!important` en el CSS personalizado salvo fuerza mayor documentada.
- **NO COLOCAR** paddings horizontales directamente sobre los elementos `.row` (esto causa desbordamientos horizontales por los márgenes negativos).