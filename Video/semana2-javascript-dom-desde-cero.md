# Semana 2: JavaScript y Manipulación del DOM desde Cero

## 1. Resumen Ejecutivo y Alcance
Este módulo cubre la sintaxis fundamental de JavaScript moderno (ES6+) y la interacción dinámica con el Document Object Model (DOM). Proporciona las reglas para gestionar estados cliente, manejo de eventos y renderizado interactivo en el navegador.

## 2. Conceptos Técnicos Clave
- **Variables y Scope**: `const` para identificadores inmutables, `let` para variables de bloque. Evitar el uso de `var`.
- **Comparación Estricta**: Diferencia entre `==` (conversión implícita de tipos) y `===` (mismo valor y mismo tipo de dato).
- **Funciones**:
  - Declarativas vs Expresadas.
  - Arrow Functions (`const sumar = (a, b) => a + b;`).
  - Funciones anónimas y Callbacks.
- **Manipulación del DOM**:
  - Selección: `document.querySelector()`, `document.querySelectorAll()`, `document.getElementById()`.
  - Propiedades: `textContent`, `innerHTML`, `value`, `classList.add()`, `classList.remove()`, `classList.toggle()`.
- **Recorrido del DOM (Traversing)**: `parentElement`, `children`, `firstElementChild`, `nextElementSibling`.
- **Manejo de Eventos**: `element.addEventListener('click', (event) => { ... })`. Uso de `event.preventDefault()` en formularios.
- **Métodos de Arreglos**: `.map()`, `.filter()`, `.find()`, `.reduce()`, `.forEach()`, `.includes()`.

## 3. Patrones de Código y Arquitectura
### Manipulación Dinámica e Interacción con el DOM
```javascript
// Selección de elementos
const formLogin = document.querySelector('#form-login');
const inputEmail = document.querySelector('#email-input');
const containerError = document.querySelector('#error-message');

// Escuchador de eventos con función flecha
formLogin.addEventListener('submit', (event) => {
  event.preventDefault(); // Prevenir recarga de página
  
  const emailValue = inputEmail.value.trim();
  
  if (!emailValue.includes('@')) {
    containerError.textContent = 'Por favor ingrese un correo válido.';
    containerError.classList.add('visible');
    return;
  }
  
  containerError.classList.remove('visible');
  console.log('Formulario procesado:', emailValue);
});
```

### Recorrido y Renderizado Dinámico de Listas
```javascript
const productos = [
  { id: 1, nombre: 'Anillo de Plata', precio: 120 },
  { id: 2, nombre: 'Collar de Oro', precio: 350 }
];

const contenedor = document.querySelector('#productos-list');

const renderProductos = (items) => {
  contenedor.innerHTML = items.map(producto => `
    <article class="card" data-id="${producto.id}">
      <h3>${producto.nombre}</h3>
      <p>Precio: $${producto.precio}</p>
      <button class="btn-comprar">Comprar</button>
    </article>
  `).join('');
};

renderProductos(productos);
```

## 4. Buenas Prácticas Requeridas
1. **Uso Exclusivo de ES6+**: Utilizar `const` por defecto; si el valor debe reasignarse, usar `let`.
2. **Comparador Estricto**: Usar siempre `===` y `!==` para evitar errores por conversión implícita de tipos.
3. **Limpieza de Strings**: Aplicar `.trim()` a los valores capturados desde campos de texto (`input.value`).
4. **Ubicación del Script**: Incluir el script al final del `<body>` o usar el atributo `defer` en el `<head>` para asegurar la carga completa del DOM.

## 5. Restricciones y Reglas de Desarrollo
- **PROHIBIDO** el uso de `var` bajo cualquier circunstancia.
- **NO UTILIZAR** `innerHTML` con entradas de usuario no sanitizadas para prevenir ataques XSS (Cross-Site Scripting). Preferir `textContent`.
- **NO ACCEDER** a propiedades de elementos que no han sido verificados previamente como existentes en el DOM (evitar `Cannot read property of null`).
- **EVITAR** la contaminación del scope global declarando variables fuera de módulos o funciones.