# Semana 3: Desarrollo Backend en Java con Servlets (Jakarta EE)

## 1. Resumen Ejecutivo y Alcance
Este módulo introduce el desarrollo web backend en Java utilizando la especificación de Servlets de Java/Jakarta EE. Cubre el ciclo de vida de peticiones HTTP (`HttpServlet`), el procesamiento de parámetros y la manipulación de headers/cookies.

## 2. Conceptos Técnicos Clave
- **HttpServlet**: Clase base abstracta de Jakarta EE para procesar peticiones HTTP.
- **Mapeo de Rutas**: Anotación `@WebServlet("/ruta")` para asociar una URL a un Servlet específico.
- **Ciclo de Vida y Métodos HTTP**:
  - `doGet(HttpServletRequest req, HttpServletResponse resp)`: Consultas de información.
  - `doPost(HttpServletRequest req, HttpServletResponse resp)`: Envío de formularios / mutación de datos.
- **Objetos Request y Response**:
  - `request.getParameter("campo")`: Obtención de parámetros del formulario o query string.
  - `response.setContentType("text/html;charset=UTF-8")`: Definición del tipo MIME de respuesta.
  - `response.getWriter()`: Obtención del `PrintWriter` para escribir el cuerpo de la respuesta.
- **Gestión de Cookies y Redirecciones**:
  - `response.addCookie(new Cookie("usuario", "Juan"))`.
  - `response.sendRedirect("otra-pagina.html")` o `response.setStatus(308)`.

## 3. Patrones de Código y Arquitectura
### Servlet Procesador de Formulario (POST)
```java
package com.curso.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        
        String usuario = request.getParameter("usuario");
        String password = request.getParameter("password");
        
        try (PrintWriter out = response.getWriter()) {
            if ("admin".equals(usuario) && "1234".equals(password)) {
                // Crear cookie de sesión simple
                Cookie sessionCookie = new Cookie("user_session", usuario);
                sessionCookie.setMaxAge(3600); // 1 hora
                response.addCookie(sessionCookie);
                
                out.println("<h2>Bienvenido, " + usuario + "</h2>");
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.println("<h2>Error: Credenciales inválidas</h2>");
            }
        }
    }
}
```

## 4. Buenas Prácticas Requeridas
1. **Configuración del Charset**: Especificar siempre `charset=UTF-8` en el `ContentType` antes de obtener el `PrintWriter`.
2. **Uso de Bloques Try-with-Resources**: Cerrar automáticamente el `PrintWriter` mediante `try (PrintWriter out = response.getWriter())`.
3. **Validación de Nulos**: Validar que `request.getParameter()` no devuelva `null` antes deinvocar métodos sobre la cadena.

## 5. Restricciones y Reglas de Desarrollo
- **PROHIBIDO** incrustar lógica compleja de negocio o de base de datos directamente dentro de los métodos del Servlet; delegar a clases de servicio.
- **NO MEZCLAR** la construcción manual extensiva de HTML dentro de código Java (los Servlets deben ser reemplazados por controladores Spring Boot y plantillas Thymeleaf/JSP en etapas superiores).
- **NO GUARDAR** información sensible (contraseñas en texto plano) en Cookies o parámetros GET.