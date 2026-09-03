# Semana 5: Asincronía y Peticiones en JavaScript (Fetch API & Async/Await)

## 1. Resumen Ejecutivo y Alcance
Este módulo aborda la gestión de la asincronía en JavaScript cliente, la evolución desde Callbacks y Promesas hasta `async`/`await`, y el consumo de servicios web mediante la API `fetch`.

## 2. Conceptos Técnicos Clave
- **Asincronía y Event Loop**: JavaScript se ejecuta en un solo hilo (`single-threaded`). Las operaciones de red o temporizadores (`setTimeout`) se delegan al navegador sin bloquear el hilo principal.
- **Evolución del Paradigma**:
  - Callbacks: Anidamiento excesivo ("Callback Hell").
  - Promesas (`Promise`): Manejo con `.then()` y `.catch()`. Estados: `pending`, `fulfilled`, `rejected`.
  - Sintaxis Moderna (`async`/`await`): Permite escribir código asíncrono con apariencia síncrona dentro de funciones marcadas con `async`.
- **Fetch API**: Función nativa del navegador para peticiones HTTP.
  - Proceso de 2 Pasos:
    1. Realizar la petición: `const response = await fetch(url);`
    2. Convertir la respuesta a JSON: `const data = await response.json();`
- **Manejo de Errores**: Uso de bloques `try...catch` para capturar fallos de red o excepciones.

## 3. Patrones de Código y Arquitectura
### Consumo Asíncrono de API con Async/Await y Renderizado
```javascript
const API_URL = 'https://jsonplaceholder.typicode.com/users';

const container = document.querySelector('#users-container');

// Función asíncrona declarada con async
const fetchUsers = async () => {
  try {
    const response = await fetch(API_URL);
    
    // Validar estado HTTP de la respuesta
    if (!response.ok) {
      throw new Error(`Error en la petición: ${response.status}`);
    }
    
    const users = await response.json();
    renderUsers(users);
  } catch (error) {
    console.error('Error al obtener usuarios:', error);
    container.innerHTML = `<p class="error">Error al cargar datos. Intente nuevamente.</p>`;
  }
};

const renderUsers = (users) => {
  container.innerHTML = users.map(user => `
    <div class="user-card">
      <h3>${user.name}</h3>
      <p>Email: ${user.email}</p>
      <p>Ciudad: ${user.address.city}</p>
    </div>
  `).join('');
};

// Llamada inicial
fetchUsers();
```

## 4. Buenas Prácticas Requeridas
1. **Manejo Obligatorio con Try/Catch**: Toda función que utilice `await` debe envolver sus operaciones de red en bloques `try...catch`.
2. **Validación de `response.ok`**: La función `fetch()` no rechaza la promesa ante códigos de estado HTTP 4xx o 5xx. Es necesario verificar explicitamente `if (!response.ok)`.
3. **Casteo a JSON Obligatorio**: Recordar siempre anteponer `await` al método `response.json()`.

## 5. Restricciones y Reglas de Desarrollo
- **PROHIBIDO** usar `await` fuera de funciones declaradas con la palabra clave `async`.
- **NO UTILIZAR** la sintaxis antigua de callbacks anidados para operaciones asíncronas complejas.
- **NO ASUMIR** que `fetch` lanzará una excepción automática cuando el servidor devuelva un código de error de cliente/servidor (404, 500).