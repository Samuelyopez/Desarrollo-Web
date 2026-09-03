# Diagrama de Clases UML - DogTor

El siguiente diagrama representa la estructura de clases principal para la clínica veterinaria **DogTor**, omitiendo los getters y setters según lo solicitado en los requisitos del Sprint 1.

```mermaid
classDiagram
    class Cliente {
        - Integer id
        - String nombre
        - String telefono
        - String email
        - String direccion
    }

    class Mascota {
        - Integer id
        - String nombre
        - String especie
        - String raza
        - Integer edad
        - Double peso
    }

    class Veterinario {
        - Integer id
        - String nombre
        - String especialidad
        - String numeroLicencia
    }

    class Cita {
        - Integer id
        - LocalDate fecha
        - LocalTime hora
        - String motivo
        - String estado
    }

    class Tratamiento {
        - Integer id
        - String descripcion
        - Double costo
        - String medicamentos
    }

    Cliente "1" -- "0..*" Mascota : tiene
    Mascota "1" -- "0..*" Cita : agenda
    Veterinario "1" -- "0..*" Cita : atiende
    Cita "1" -- "0..*" Tratamiento : genera
```

Este modelo inicial se usará para construir las entidades (POJOs) en los sprints futuros utilizando Spring Boot y las anotaciones de Lombok.
