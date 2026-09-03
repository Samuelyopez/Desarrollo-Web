# Semana 4: Spring Boot y Thymeleaf (CRUD Completo en Capas)

## 1. Resumen Ejecutivo y Alcance
Este módulo abarca el desarrollo de aplicaciones web Full Stack estructuradas bajo el patrón MVC y la arquitectura en capas con Spring Boot 3.x y el motor de plantillas Thymeleaf. Define las reglas para la inyección de dependencias, controladores, servicios y navegación dinámica.

## 2. Conceptos Técnicos Clave
- **Spring Boot 3.x**: Framework backend simplificado con servidor embebido (Tomcat) e inyección de dependencias.
- **Arquitectura en Capas**:
  - `@Controller`: Manejo de rutas, binding del modelo (`Model`) y retorno de nombres de vistas Thymeleaf.
  - `@Service`: Lógica de negocio y reglas del dominio.
  - `@Repository` / Falsa BD: Acceso y persistencia de datos.
  - `Entities`: Objetos del dominio enriquecidos con Lombok (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`).
- **Motor de Plantillas Thymeleaf**:
  - `th:text="${variable}"`: Inserción dinámica de texto.
  - `th:each="item : ${lista}"`: Iteración de colecciones.
  - `th:field="*{atributo}"`: Binding bidireccional en formularios con `th:object`.
  - `th:href="@{/ruta(param=${val})}"`: Generación dinámica de URLs.
  - `th:if="${condicion}"`: Renderizado condicional.
  - `th:fragment` / `th:replace`: Reutilización de componentes de interfaz (Navbar, Footer).

## 3. Patrones de Código y Arquitectura
### Controlador Spring Boot MVC
```java
package com.example.demo.controller;

import com.example.demo.entities.Student;
import com.example.demo.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public String listStudents(Model model) {
        model.addAttribute("estudiantes", studentService.searchAll());
        return "mostrar-todos-estudiantes";
    }

    @GetMapping("/add")
    public String showFormCreate(Model model) {
        // Enviar objeto vacío con ID nulo para creación
        Student emptyStudent = Student.builder().id(null).semestre(1).build();
        model.addAttribute("estudiante", emptyStudent);
        return "formulario-estudiante";
    }

    @PostMapping("/add")
    public String saveStudent(@ModelAttribute("estudiante") Student student) {
        studentService.save(student);
        return "redirect:/student";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable("id") Long id) {
        studentService.delete(id);
        return "redirect:/student";
    }
}
```

### Formulario Thymeleaf con Campo Oculto (Create/Update)
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
  <meta charset="UTF-8">
  <title>Formulario Estudiante</title>
</head>
<body>
  <!-- Reutilización de fragmento Navbar -->
  <div th:replace="~{fragmentos :: navbar}"></div>

  <div class="container">
    <h2 th:text="${estudiante.id == null} ? 'Crear Estudiante' : 'Actualizar Estudiante'"></h2>

    <form th:action="@{/student/add}" th:object="${estudiante}" method="post">
      <!-- ID Oculto indispensable para diferenciar creación de edición -->
      <input type="hidden" th:field="*{id}" />

      <label>Nombre:</label>
      <input type="text" th:field="*{name}" required />

      <label>Correo:</label>
      <input type="email" th:field="*{email}" required />

      <button type="submit">Guardar</button>
    </form>
  </div>
</body>
</html>
```

## 4. Buenas Prácticas Requeridas
1. **Diferenciación de IDs Nulos**: Para crear un nuevo registro, el objeto debe enviarse con `id = null`. Si el `id` tiene un valor, se trata de una actualización (`update`).
2. **Inyección por Constructor**: Utilizar `@RequiredArgsConstructor` de Lombok en lugar de `@Autowired` sobre campos privados para asegurar inmutabilidad y facilidad de pruebas.
3. **Patrón Redirect-After-Post**: Todos los métodos `@PostMapping` exitosos deben retornar una redirección (`return "redirect:/ruta";`) para prevenir reenvíos dobles del formulario al recargar.
4. **Fragmentación de Layouts**: Extraer código repetido (`navbar`, `footer`) a un archivo `fragmentos.html` e importarlo con `th:replace`.

## 5. Restricciones y Reglas de Desarrollo
- **PROHIBIDO** saltarse capas (ej. llamar al repositorio directamente desde el controlador sin pasar por el servicio).
- **NO MANTENER** rutas `@GetMapping` duplicadas para la misma URL dentro de la aplicación.
- **NO OLVIDAR** el campo oculto `<input type="hidden" th:field="*{id}" />` en formularios compartidos de creación/edición.