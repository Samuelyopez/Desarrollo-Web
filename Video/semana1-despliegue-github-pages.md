# Semana 1: Despliegue y Alojamiento con GitHub Pages

## 1. Resumen Ejecutivo y Alcance
Este módulo aborda el proceso de despliegue continuo y hosting estático utilizando GitHub Pages. Proporciona las reglas y restricciones para publicar aplicaciones web cliente (HTML/CSS/JS o frameworks de renderizado en cliente como Angular/React/Vue) directamente desde un repositorio de GitHub.

## 2. Conceptos Técnicos Clave
- **Client-Side Rendering (CSR)**: GitHub Pages sirve archivos estáticos pre-compilados (HTML, CSS, JS, imágenes). El cliente (navegador) ejecuta todo el código JavaScript.
- **Estructura en Raíz (Root Directory)**: El servidor web por defecto de GitHub Pages busca el archivo `index.html` en la raíz del repositorio o dentro de la carpeta `/docs`.
- **Rutas Relativas vs Absolutas**:
  - Rutas absolutas con slash inicial (`/images/foto.jpg`) fallan en GitHub Pages porque apuntan al dominio raíz (`username.github.io/images/foto.jpg`) ignorando el subdirectorio del repositorio (`username.github.io/repo-name/images/foto.jpg`).
  - Rutas relativas (`images/foto.jpg` o `./images/foto.jpg`) funcionan correctamente en cualquier subdominio o subruta.
- **GitHub Actions Workflows**: Automatización del build y despliegue cada vez que se realiza un `git push` a la rama principal (`main` / `master`).

## 3. Patrones de Código y Configuración
### Estructura del Repositorio Estático
```text
mi-proyecto-web/
├── index.html           <-- Obligatorio en la raíz
├── css/
│   └── styles.css
├── js/
│   └── main.js
└── images/
    └── logo.png
```

### Configuración en GitHub Pages (Settings -> Pages)
1. **Source**: Deploy from a branch.
2. **Branch**: `main` / `root` (`/`).
3. **Save**: Genera automáticamente la URL pública `https://<username>.github.io/<repository-name>/`.

## 4. Buenas Prácticas Requeridas
1. **Uso de Rutas Relativas**: Todas las referencias a recursos estáticos (`href`, `src`) deben ser relativas (`./css/styles.css` o `css/styles.css`).
2. **Verificación Local Previa**: Probar la aplicación en local utilizando servidores locales (ej. Live Server o `npx serve`) antes de realizar push a la rama de producción.
3. **Nombres de Archivo Sensibles a Mayúsculas**: Git y Linux (servidor de GitHub Pages) son sensibles a mayúsculas/minúsculas (`Case-Sensitive`). Asegurar coincidencia exacta entre nombres de archivos y rutas escritas en HTML/CSS.

## 5. Restricciones y Reglas de Desarrollo
- **PROHIBIDO** intentar desplegar código de backend de servidor como Spring Boot (Java), Node.js (Express), PHP o Python (Django/Flask) en GitHub Pages.
- **NO USAR** enlaces con slash inicial (`/`) para assets internos (`<img src="/img/logo.png">` romperá en producción).
- **NO INCLUIR** archivos temporales, ejecutables o claves secretas de API (`.env` con secrets) en el repositorio público.
- **RESTRICCIÓN TÉCNICA**: El límite de ancho de banda gratuito es de 100 GB por mes y el tamaño máximo del sitio es de 1 GB.