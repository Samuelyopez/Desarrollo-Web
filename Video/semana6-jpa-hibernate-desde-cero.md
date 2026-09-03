# Semana 6: Persistencia de Datos con JPA e Hibernate

## 1. Resumen Ejecutivo y Alcance
Este módulo abarca la capa de persistencia mediante JPA (Java Persistence API) e Hibernate en Spring Boot. Cubre el mapeo objeto-relacional (ORM), definición de entidades, relaciones relacionales, repositorios y el manejo transaccional.

## 2. Conceptos Técnicos Clave
- **ORM (Object-Relational Mapping)**: Sincronización automática entre clases Java y tablas de la base de datos SQL sin escribir SQL manual.
- **Base de Datos H2**: Base de datos SQL en memoria ideal para entornos de desarrollo y pruebas rápidas.
- **Anotaciones de Entidad JPA**:
  - `@Entity`: Marca la clase como tabla mapeada.
  - `@Table(name = "estudiantes")`: Especifica el nombre explícito de la tabla.
  - `@Id`: Marca el campo como Clave Primaria.
  - `@GeneratedValue(strategy = GenerationType.IDENTITY)`: Autoincremento gestionado por la BD.
  - `@Column(name, length, nullable, unique)`: Restricciones a nivel de columna.
- **Mapeo de Relaciones**:
  - `@ManyToOne`: Muchas entidades apuntan a una (ej. Varios Estudiantes pertenecen a una Carrera).
  - `@OneToMany(mappedBy = "carrera", cascade = CascadeType.ALL)`: Una entidad posee muchas (Lado débil).
  - `@ManyToMany`: Relación de muchos a muchos con tabla intermedia.
- **Spring Data JPA**: Interface `JpaRepository<Entidad, ID>` que provee operaciones CRUD automáticas (`findAll()`, `findById()`, `save()`, `deleteById()`).
- **Manejo Global de Excepciones**: `@ControllerAdvice` y `@ExceptionHandler` para capturar errores del ORM o de búsqueda y presentar páginas de error amigables.

## 3. Patrones de Código y Arquitectura
### Entidad JPA Mapeada con Relación @ManyToOne
```java
package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_name", length = 50, nullable = false)
    private String name;

    @Column(length = 70, nullable = false, unique = true)
    private String email;

    private Integer semestre;

    @ManyToOne
    @JoinColumn(name = "carrera_id")
    private Carrera carrera;
}
```

### Repositorio Spring Data JPA
```java
package com.example.demo.repository;

import com.example.demo.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    // Métodos CRUD básicos listos sin escribir código SQL
}
```

### Servicio Transaccional con Manejo de Excepciones
```java
package com.example.demo.service;

import com.example.demo.entities.Student;
import com.example.demo.errors.StudentNotFoundException;
import com.example.demo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Student> searchAll() {
        return studentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Student searchById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    @Override
    @Transactional
    public Student save(Student student) {
        return studentRepository.save(student);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Student student = searchById(id);
        studentRepository.delete(student);
    }
}
```

## 4. Buenas Prácticas Requeridas
1. **Evitar `@Data` en Entidades Bidireccionales**: Usar `@Getter` y `@Setter` explícitos. La anotación `@Data` genera `equals()`, `hashCode()` y `toString()` que causan bucles infinitos (`StackOverflowError`) en relaciones bidireccionales.
2. **Uso de `@Transactional`**: Anotar todos los métodos de modificación en la capa de servicio con `@Transactional` de Spring.
3. **Llaves Primarias de Tipo `Long`**: Usar tipos envolventes `Long` (en lugar de `int` o `long` primitivo) para permitir valores nulos antes de la persistencia.
4. **Captura de Excepciones mediante `@ControllerAdvice`**: Retornar vistas de error personalizadas ante búsquedas fallidas (`findById`).

## 5. Restricciones y Reglas de Desarrollo
- **PROHIBIDO** invocar directamente los métodos del `JpaRepository` desde el Controlador.
- **NO UTILIZAR** tipos primitivos (`int`, `long`) para la Clave Primaria (`@Id`).
- **NO MANTENER** relaciones bidireccionales sin especificar `mappedBy` en uno de los lados para evitar creación de tablas intermedias redundantes.
- **NO IGNORAR** la restricción de registros únicos (`unique = true`) en la capa de base de datos.