# Plan de mejoras — Proyecto Sprint3 (Dogtor)

> Basado en la comparación con "Clase 6" (guía del profesor) y "TheRoyale" (proyecto de máxima calificación).
> **No incluye**: pruebas unitarias, inyección de dependencias por constructor, ni capa de seguridad (hashing de contraseñas / Spring Security).

---

## Índice

1. [Limpieza de código y archivos](#1-limpieza-de-código-y-archivos)
2. [Funcionalidades incompletas o inalcanzables desde la vista](#2-funcionalidades-incompletas-o-inalcanzables-desde-la-vista)
3. [Base de datos](#3-base-de-datos)
4. [Código de la capa de servicio y controladores](#4-código-de-la-capa-de-servicio-y-controladores)
5. [Experiencia de usuario (UX)](#5-experiencia-de-usuario-ux)

---

## 1. Limpieza de código y archivos

### 1.1 Archivos que no deben versionarse ni entregarse

**Problema:** el proyecto incluye la carpeta `target/` (artefactos compilados de Maven, ~568 KB) y varios archivos de base de datos H2 duplicados y desincronizados: `veterinariadb.mv.db` en la raíz **y** dentro de `data/`, más `veterinariadb.lock.db`.

**Cómo corregirlo:**
- Verifica que tu `.gitignore` real (el que se aplica antes de comprimir para entrega) contenga:
  ```gitignore
  target/
  *.db
  *.lock.db
  ```
- Borra físicamente `target/`, `data/veterinariadb.mv.db`, `data/veterinariadb.lock.db` y el `.mv.db` suelto en la raíz antes de comprimir el proyecto para entregar.
- Para regenerar `target/` en cualquier momento basta con `./mvnw clean package`; nunca debe entregarse a mano.
- Si necesitas conservar datos de prueba reproducibles, usa `DataInitializer` (que ya tienes) en vez de versionar el archivo `.db`.

### 1.2 Imports no usados

**Problema:** `ProductoServiceImpl.java` y `SolicitudAdopcionServiceImpl.java` importan `ResourceNotFoundException` pero nunca la lanzan.

**Cómo corregirlo:** dos opciones válidas, elige una y sé consistente:

**Opción A — Eliminar el import** (si esos métodos no necesitan lanzar la excepción):
```java
// Borra esta línea si no se usa:
import com.veterinaria.dogtor.errores.ResourceNotFoundException;
```

**Opción B — Usarla de verdad** (recomendado, por consistencia con el resto del proyecto):
```java
@Override
public Producto searchById(Long id) {
    return productoRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + id));
}
```
Aplica el mismo criterio a `SolicitudAdopcionServiceImpl`.

---

## 2. Funcionalidades incompletas o inalcanzables desde la vista

Estos son los casos más importantes: código que existe y compila, pero que el usuario final **nunca puede usar** porque falta la conexión entre backend y vista.

### 2.1 Entidades sin ninguna capa web (`Cita`, `HistorialMedico`, `Veterinario`)

**Problema:** las tres entidades y sus repositorios existen y crean tablas en la base de datos, pero no tienen `Service` ni `Controller`. El formulario público `/citas` tiene un `<form>` sin `action` y un botón `type="button"` — no envía nada.

**Decisión que ya tomaron:** estas entidades **no seguirán existiendo** en el modelo. Por lo tanto, la acción correcta es **eliminarlas por completo**, no dejarlas a medias:

1. Borra `Cita.java`, `HistorialMedico.java`, `Veterinario.java` de `entidad/`.
2. Borra `CitaRepository.java`, `HistorialMedicoRepository.java`, `VeterinarioRepository.java` de `repositorio/`.
3. En `DataInitializer.java`, elimina el parámetro `VeterinarioRepository veterinarioRepository` del método `loadData(...)` y su import.
4. En `citas.html`, deja el formulario como contenido puramente informativo (por ejemplo, un texto "Llámanos al [teléfono] para agendar tu cita") o elimina la ruta `/citas` del `WebController` y el enlace de navegación, para no prometer una funcionalidad que no existe.

### 2.2 `Dueno.telefono` y `Dueno.direccion` nunca se pueden completar

**Problema:** la entidad tiene esos dos campos, pero ni `registro.html`, ni `editar-perfil.html`, ni `admin-editar-dueno.html` incluyen un `<input>` para ellos. Quedan `null` para siempre en cualquier cuenta creada desde la aplicación real.

**Cómo corregirlo:** agrega los campos al formulario de registro y de edición.

```html
<!-- registro.html y editar-perfil.html -->
<div>
    <label class="block text-sm font-bold text-gray-700 mb-1">Teléfono</label>
    <input type="tel" id="telefono" name="telefono"
           th:field="*{telefono}"  <!-- solo en editar-perfil, en registro usa name="telefono" -->
           class="w-full p-3 border rounded-lg">
</div>
<div>
    <label class="block text-sm font-bold text-gray-700 mb-1">Dirección</label>
    <input type="text" id="direccion" name="direccion"
           th:field="*{direccion}"
           class="w-full p-3 border rounded-lg">
</div>
```

En `DuenoController.procesarRegistro` y `guardarPerfilEditar` no necesitas cambiar nada de lógica: como usas `@ModelAttribute Dueno`, Spring ya bindea automáticamente los campos nuevos por nombre. Solo asegúrate de copiar `telefono`/`direccion` también en `guardarPerfilEditar`, donde hoy solo se copian `nombre`, `correo` y `password`:

```java
dbDueno.setNombre(formDueno.getNombre());
dbDueno.setCorreo(formDueno.getCorreo());
dbDueno.setPassword(formDueno.getPassword());
dbDueno.setTelefono(formDueno.getTelefono());   // agregar
dbDueno.setDireccion(formDueno.getDireccion()); // agregar
```

### 2.3 Mascotas "eliminadas" quedan inalcanzables para siempre

**Problema:** el botón "Borrar" en `admin-mascotas.html` desactiva la mascota (`activo = false`), pero:
- `MascotaRepository` no tiene ningún método para consultar las inactivas.
- No hay endpoint ni vista para verlas ni reactivarlas.
- El texto del `confirm()` dice "¿Está seguro de **eliminar** esta mascota?", lo cual es engañoso porque no se elimina.

**Cómo corregirlo — enfoque recomendado:** mostrar ambos estados en el mismo listado admin, con una etiqueta visual, y un botón que alterna el estado.

**Repositorio** — agrega un método para traer todo, activas e inactivas:
```java
public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    List<Mascota> findByDuenoAndActivoTrue(Dueno dueno);
    List<Mascota> findByEnAdopcionAndActivoTrue(boolean enAdopcion);
    List<Mascota> findByActivoTrue();
    // nuevo: para el panel de administración, sin filtrar por activo
    List<Mascota> findAllByOrderByActivoDescIdAsc();
}
```

**Service** — agrega un método para alternar el estado en vez de solo desactivar:
```java
// MascotaService (interfaz)
void toggleActivo(Long id);

// MascotaServiceImpl
@Override
public void toggleActivo(Long id) {
    Mascota mascota = searchById(id);
    mascota.setActivo(!mascota.isActivo());
    if (!mascota.isActivo()) {
        mascota.setEnAdopcion(false); // una mascota inactiva no debería listarse en adopción
    }
    mascotaRepository.save(mascota);
}
```

**Controller:**
```java
@GetMapping("/admin/mascotas")
public String adminMascotas(HttpSession session, Model model) {
    if (!isAdmin(session)) return "redirect:/login";
    model.addAttribute("mascotas", mascotaService.searchAllIncluyendoInactivas()); // usa el nuevo método
    return "admin-mascotas";
}

@GetMapping("/admin/mascotas/toggle/{id}")
public String adminToggleMascota(@PathVariable("id") Long id, HttpSession session) {
    if (!isAdmin(session)) return "redirect:/login";
    mascotaService.toggleActivo(id);
    return "redirect:/admin/mascotas";
}
```

**Vista** — etiqueta + botón dinámico:
```html
<tr th:each="mascota : ${mascotas}">
    <td th:text="${mascota.nombre}"></td>
    <td>
        <span th:if="${mascota.activo}" class="px-2 py-1 rounded-full bg-green-100 text-green-700 text-xs font-bold">Activa</span>
        <span th:unless="${mascota.activo}" class="px-2 py-1 rounded-full bg-gray-200 text-gray-500 text-xs font-bold">Inactiva</span>
    </td>
    <td>
        <a th:href="@{/admin/mascotas/toggle/{id}(id=${mascota.id})}"
           th:text="${mascota.activo} ? 'Desactivar' : 'Reactivar'"
           th:onclick="${mascota.activo}
               ? '''return confirm(\'¿Desactivar esta mascota? No se eliminará, solo dejará de aparecer en el sitio público.\')'''
               : null"
           class="text-red-500 hover:underline font-bold text-sm"></a>
    </td>
</tr>
```

### 2.4 Las solicitudes de adopción se guardan pero nadie las ve

**Problema:** `SolicitudAdopcionService` solo tiene el método `save()`. No existe ningún controller ni vista para listar solicitudes, y el campo `estado` (`PENDIENTE` por defecto) nunca cambia — un usuario llena el formulario y la solicitud desaparece en la base de datos sin que ningún admin la vea.

**Cómo corregirlo:**

**Service — agrega los métodos que faltan:**
```java
// SolicitudAdopcionService
List<SolicitudAdopcion> searchAll();
SolicitudAdopcion searchById(Long id);
void actualizarEstado(Long id, String nuevoEstado);

// SolicitudAdopcionServiceImpl
@Override
public List<SolicitudAdopcion> searchAll() {
    return repository.findAll();
}

@Override
public SolicitudAdopcion searchById(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada: " + id));
}

@Override
@Transactional
public void actualizarEstado(Long id, String nuevoEstado) {
    SolicitudAdopcion solicitud = searchById(id);
    solicitud.setEstado(nuevoEstado);
    repository.save(solicitud);
}
```

**Controller nuevo (o agregado a `WebController`):**
```java
@GetMapping("/admin/solicitudes")
public String adminSolicitudes(HttpSession session, Model model) {
    if (!isAdmin(session)) return "redirect:/login";
    model.addAttribute("solicitudes", solicitudService.searchAll());
    return "admin-solicitudes";
}

@PostMapping("/admin/solicitudes/estado/{id}")
public String adminCambiarEstado(@PathVariable("id") Long id,
                                  @RequestParam("estado") String estado,
                                  HttpSession session) {
    if (!isAdmin(session)) return "redirect:/login";
    solicitudService.actualizarEstado(id, estado);
    return "redirect:/admin/solicitudes";
}
```

**Vista nueva `admin-solicitudes.html`:** tabla con adoptante, mascota solicitada, motivo, estado actual, y un `<select>` + botón para cambiar el estado (`PENDIENTE`, `APROBADA`, `RECHAZADA`). Cuando el estado pase a `APROBADA`, aprovecha para actualizar también la mascota:
```java
if ("APROBADA".equals(nuevoEstado)) {
    Mascota mascota = solicitud.getMascota();
    mascota.setEnAdopcion(false);
    mascota.setDueno(solicitud.getDueno()); // transferir la mascota al adoptante
    mascotaRepository.save(mascota);
}
```

### 2.5 El carrito de compras no lleva a ningún lado

**Problema:** el botón "Ir a Pagar" existe en el HTML y `script.js` lo referencia solo para habilitarlo/deshabilitarlo — no tiene ningún `addEventListener` de clic. El carrito completo vive en `localStorage` del navegador; no existe entidad `Pedido`/`Orden` en el backend.

**Cómo corregirlo (alcance mínimo razonable para un proyecto de curso):**

1. Agrega una entidad simple de pedido:
```java
@Entity
public class Pedido {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dueno_id")
    private Dueno dueno;

    private LocalDateTime fecha;
    private Double total;
    private String estado; // PENDIENTE, PAGADO, ENTREGADO

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<PedidoItem> items;
}

@Entity
public class PedidoItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private Integer cantidad;
    private Double precioUnitario;
}
```

2. En `script.js`, conecta el botón a un endpoint real que reciba el carrito (por ejemplo, en formato JSON) en vez de dejarlo sin `addEventListener`:
```javascript
checkoutBtn.addEventListener('click', async () => {
    const response = await fetch('/pedidos/crear', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ items: cart })
    });
    if (response.ok) {
        cart = [];
        saveCart();
        updateCartUI();
        window.location.href = '/pedidos/confirmacion';
    }
});
```

3. Si el alcance del sprint no permite implementar todo el flujo de pedidos, la alternativa honesta es **quitar visualmente el botón "Ir a Pagar"** (o dejarlo deshabilitado permanentemente con un texto tipo "Próximamente") para no prometer una función que no existe.

### 2.6 Inconsistencia entre borrado de `Dueno` (físico) y `Mascota` (lógico)

**Problema:** `DuenoServiceImpl.delete()` borra físicamente al dueño y a su `Usuario` asociado, y el controlador borra también sus `SolicitudAdopcion` con `deleteByDueno()`. En cambio, sus mascotas solo se desactivan. Mismo botón "Eliminar", dos políticas distintas, y se pierde el historial de solicitudes de adopción justo cuando decidiste preservarlo para mascotas.

**Cómo corregirlo:** aplica la misma política (soft-delete) de forma consistente.

```java
// Usuario.java — agregar
@Column(nullable = false)
@Builder.Default
private boolean activo = true;

// Dueno.java — agregar
@Column(nullable = false)
@Builder.Default
private boolean activo = true;
```

```java
// DuenoServiceImpl
@Override
@Transactional
public void delete(Long id) {
    Dueno dueno = duenoRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Dueño no encontrado: " + id));

    dueno.setActivo(false);
    if (dueno.getUsuario() != null) {
        dueno.getUsuario().setActivo(false);
    }
    duenoRepository.save(dueno);

    // desactivar sus mascotas también, en vez de borrarlas
    List<Mascota> mascotas = mascotaRepository.findByDuenoAndActivoTrue(dueno);
    mascotas.forEach(m -> { m.setActivo(false); m.setEnAdopcion(false); });
    mascotaRepository.saveAll(mascotas);

    // conservar las solicitudes de adopción, NO borrarlas
}
```
Y actualiza `DuenoRepository.searchAll()`/consultas relacionadas para filtrar por `activo = true` donde corresponda (login, listados públicos), igual que ya haces con `Mascota`.

---

## 3. Base de datos

### 3.1 `Dueno.usuario_id` debe ser `UNIQUE` a nivel de columna

**Problema:** la relación está anotada como `@OneToOne`, pero sin `unique = true` en el `@JoinColumn`, nada impide que dos `Dueno` distintos apunten al mismo `Usuario` a nivel de base de datos.

**Corrección:**
```java
@OneToOne
@JoinColumn(name = "usuario_id", unique = true)
private Usuario usuario;
```
Aplica lo mismo en `Veterinario` si decides conservar esa entidad (aunque, según lo acordado, se eliminará).

### 3.2 Reemplazar `String` libres por `enum` donde el valor es un conjunto cerrado

**Problema:** `SolicitudAdopcion.estado`, `Producto.categoria` y `Usuario.rol` son `String` sin restricción — se pueden guardar valores mal escritos (`"pendiente"`, `"Pendiente "`, typos) que rompen silenciosamente cualquier `WHERE estado = 'PENDIENTE'`.

**Corrección — ejemplo con `estado`:**
```java
public enum EstadoSolicitud {
    PENDIENTE, APROBADA, RECHAZADA
}
```
```java
// SolicitudAdopcion.java
@Enumerated(EnumType.STRING)
@Column(nullable = false, length = 20)
private EstadoSolicitud estado;
```
Repite el mismo patrón para:
```java
public enum CategoriaProducto { MEDICINAL, RECREACIONAL }
public enum RolUsuario { ADMIN, DUENO }
```
`EnumType.STRING` es importante (no uses `ORDINAL`): guarda el nombre del enum como texto legible en la base de datos, y si más adelante agregas un valor nuevo al enum no rompe los datos existentes.

### 3.3 `Mascota.edad` debe ser un dato numérico, no texto libre

**Problema:** hoy se guarda como `"6 meses"`, `"2 años"` — imposible de ordenar, filtrar o promediar.

**Corrección recomendada — usar fecha de nacimiento en vez de edad:**
```java
// Mascota.java
@Column
private LocalDate fechaNacimiento;

// Método calculado, no persistido:
@Transient
public int getEdadEnAnios() {
    if (fechaNacimiento == null) return 0;
    return Period.between(fechaNacimiento, LocalDate.now()).getYears();
}
```
Si prefieres no migrar a fecha de nacimiento por ahora (menos cambios en formularios existentes), la alternativa mínima es:
```java
private Integer edadAnios; // entero simple, sin unidad mezclada
```
y formatear el texto ("2 años") solo en la vista con Thymeleaf, nunca en la base de datos.

### 3.4 Agregar `cascade`/`OnDelete` explícito en las relaciones

**Problema:** ninguna relación declara qué debe pasar en la base de datos cuando se borra el registro "padre". Hoy la integridad se resuelve a mano dentro de los controllers (borrando uno por uno en un `for`), lo cual es frágil: si alguien llama al repositorio directamente sin pasar por ese controller, la regla no se aplica.

**Corrección** (ejemplo `Dueno → Mascota`, usando la anotación de Hibernate que ya usa Clase 6):
```java
// Mascota.java
@ManyToOne
@JoinColumn(name = "dueno_id")
@OnDelete(action = OnDeleteAction.SET_NULL) // o CASCADE, según la regla de negocio
private Dueno dueno;
```
> Nota: si adoptas el soft-delete consistente del punto 2.6, este `OnDelete` físico deja de ser tan crítico porque ya no harás `DELETE` reales — pero sigue siendo buena práctica declararlo para el caso en que alguien limpie datos directamente por SQL.

### 3.5 Agregar índices en columnas de búsqueda frecuente

**Problema:** columnas como `Mascota.dueno_id`, `Mascota.enAdopcion`, `SolicitudAdopcion.dueno_id`/`mascota_id` no tienen índice explícito. Con pocos datos no se nota; con cientos de registros sí.

**Corrección:**
```java
@Entity
@Table(name = "mascota", indexes = {
    @Index(name = "idx_mascota_dueno", columnList = "dueno_id"),
    @Index(name = "idx_mascota_activo_adopcion", columnList = "activo, enAdopcion")
})
public class Mascota { ... }
```

### 3.6 Normalizar el campo `vacunas`

**Problema:** es un `String` único (`"Al día"`, `"Desparasitado"`) para lo que conceptualmente es una lista (una mascota puede tener varias vacunas con fechas distintas).

**Corrección mínima (sin recrear todo `HistorialMedico`):**
```java
@Entity
public class Vacuna {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    private LocalDate fechaAplicacion;

    @ManyToOne
    @JoinColumn(name = "mascota_id")
    private Mascota mascota;
}
```
Y en `Mascota`, reemplazar `private String vacunas;` por:
```java
@OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Vacuna> vacunas = new ArrayList<>();
```

### 3.7 Agregar campos de auditoría

**Problema:** ninguna entidad registra cuándo fue creada o modificada. Para un panel de administración esto es prácticamente indispensable.

**Corrección — usando anotaciones de Hibernate (sin librerías extra):**
```java
@Entity
public class Mascota {
    // ... campos existentes

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;
}
```
Aplica lo mismo a `Dueno`, `Producto` y `SolicitudAdopcion` como mínimo.

### 3.8 `ddl-auto`: elegir una estrategia consciente

**Problema:** usas `spring.jpa.hibernate.ddl-auto=update`, mientras Clase 6 y TheRoyale usan `create-drop`. `update` nunca elimina columnas obsoletas (por ejemplo, si borras la entidad `Cita` del punto 2.1, la tabla `cita` queda huérfana en el `.mv.db` hasta que borres el archivo a mano).

**Corrección:** si el proyecto es solo para desarrollo/demo académica, cambia a:
```properties
spring.jpa.hibernate.ddl-auto=create-drop
```
Si necesitas persistencia real entre reinicios del servidor (por ejemplo, para no perder los datos que un usuario real cargó), mantén `update`, pero:
- Borra manualmente el archivo `.mv.db` cada vez que hagas un cambio estructural grande en las entidades (como eliminar `Cita`/`Veterinario`/`HistorialMedico`).
- Considera introducir Flyway o Liquibase más adelante si el curso lo permite, para versionar los cambios de esquema en vez de depender de `ddl-auto`.

---

## 4. Código de la capa de servicio y controladores

### 4.1 Falta paginación en los listados administrativos

**Problema:** `DataInitializer` siembra 50 dueños y ~106 mascotas. `admin-mascotas.html` y `admin-duenos.html` renderizan la tabla completa sin paginar.

**Corrección:**
```java
// MascotaRepository
Page<Mascota> findByActivoTrue(Pageable pageable);
```
```java
// MascotaController
@GetMapping("/admin/mascotas")
public String adminMascotas(@RequestParam(defaultValue = "0") int page,
                             HttpSession session, Model model) {
    if (!isAdmin(session)) return "redirect:/login";
    Page<Mascota> pagina = mascotaService.searchAll(PageRequest.of(page, 15));
    model.addAttribute("mascotas", pagina.getContent());
    model.addAttribute("paginaActual", page);
    model.addAttribute("totalPaginas", pagina.getTotalPages());
    return "admin-mascotas";
}
```
```html
<!-- admin-mascotas.html, al final de la tabla -->
<div class="flex justify-center gap-2 mt-4">
    <a th:each="i : ${#numbers.sequence(0, totalPaginas - 1)}"
       th:href="@{/admin/mascotas(page=${i})}"
       th:text="${i + 1}"
       th:classappend="${i == paginaActual} ? 'font-bold text-primary' : ''"></a>
</div>
```

### 4.2 Falta buscador/filtro en los listados

**Problema:** con más de 100 mascotas, encontrar una específica requiere revisar toda la tabla.

**Corrección:**
```java
// MascotaRepository
List<Mascota> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
```
```java
// MascotaController
@GetMapping(params = "nombre")
public String buscarMascotas(@RequestParam String nombre, Model model) {
    model.addAttribute("mascotas", mascotaService.buscarPorNombre(nombre));
    return "admin-mascotas";
}
```
```html
<form method="get" action="/admin/mascotas" class="mb-4">
    <input type="text" name="nombre" placeholder="Buscar por nombre..."
           class="p-2 border rounded-lg">
    <button type="submit" class="bg-primary text-white px-4 py-2 rounded-lg">Buscar</button>
</form>
```

### 4.3 Sin mensajes de éxito (`RedirectAttributes`)

**Problema:** después de guardar, editar o borrar, el usuario es redirigido en silencio, sin ninguna confirmación.

**Corrección** (siguiendo el patrón que ya usa `AutenticacionController` de TheRoyale):
```java
@PostMapping("/admin/mascotas/guardar")
public String adminGuardarMascota(@ModelAttribute Mascota mascota,
                                   @RequestParam(value = "duenoId", required = false) Long duenoId,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
    if (!isAdmin(session)) return "redirect:/login";
    // ... lógica existente
    mascotaService.save(mascota);
    redirectAttributes.addFlashAttribute("mensaje", "Mascota guardada correctamente.");
    return "redirect:/admin/mascotas";
}
```
```html
<!-- admin-mascotas.html, arriba de la tabla -->
<div th:if="${mensaje}" class="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-4" th:text="${mensaje}"></div>
```
Repite este patrón en todos los `@PostMapping` de creación/edición/borrado de `DuenoController`, `MascotaController` y `WebController`.

### 4.4 Validación de formularios con `@Valid` y `BindingResult`

**Problema:** solo 2 atributos `required` existen en todo `admin-formulario-mascota.html`, y no hay ningún `@Valid` en el backend. Hoy se puede guardar una mascota sin nombre o un producto con precio negativo.

**Corrección — anotar la entidad:**
```java
@Entity
public class Mascota {
    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "La especie es obligatoria")
    private String especie;

    @PositiveOrZero(message = "El peso no puede ser negativo")
    private Double peso;
    // ...
}
```
**Corrección — validar en el controller:**
```java
@PostMapping("/admin/mascotas/guardar")
public String adminGuardarMascota(@Valid @ModelAttribute("mascota") Mascota mascota,
                                   BindingResult bindingResult,
                                   @RequestParam(value = "duenoId", required = false) Long duenoId,
                                   HttpSession session,
                                   Model model) {
    if (!isAdmin(session)) return "redirect:/login";

    if (bindingResult.hasErrors()) {
        model.addAttribute("duenos", duenoService.searchAll());
        return "admin-formulario-mascota"; // vuelve al formulario mostrando los errores
    }
    // ... resto de la lógica
}
```
```html
<!-- admin-formulario-mascota.html -->
<input type="text" th:field="*{nombre}" class="w-full p-3 border rounded-lg">
<span th:if="${#fields.hasErrors('nombre')}" th:errors="*{nombre}" class="text-red-500 text-sm"></span>
```
Necesitas agregar la dependencia `spring-boot-starter-validation` al `pom.xml` si aún no la tienes:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

### 4.5 `MascotaServiceImpl.delete()` sin `@Transactional`

**Problema:** el método combina varias escrituras (borrar solicitudes asociadas, actualizar la mascota) sin una transacción explícita. Si una operación falla a mitad de camino, puede quedar en un estado inconsistente.

**Corrección:**
```java
@Override
@Transactional
public void toggleActivo(Long id) {
    // ... (ver punto 2.3)
}
```
Aplica `@Transactional` a cualquier método de servicio que combine más de una operación de escritura, incluyendo el nuevo `DuenoServiceImpl.delete()` del punto 2.6.

---

## 5. Experiencia de usuario (UX)

### 5.1 Accesibilidad: falta el atributo `alt` en casi todas las imágenes

**Problema (conteo real):** `index.html` (3 imgs, 0 `alt`), `pacientes.html` (2 imgs, 0 `alt`), `farmacia.html`, `accesorios.html`, `adopciones.html`, `detalle-mascota.html`, `formulario-adopcion.html` — todas sin ningún `alt`. Solo `equipo.html`, `nosotros.html`, `perfil.html` y `admin-mascotas.html` sí los tienen.

**Corrección:**
```html
<!-- Antes -->
<img th:src="${mascota.fotoUrl}" class="w-48 h-48 rounded-2xl object-cover shadow-md">

<!-- Después -->
<img th:src="${mascota.fotoUrl}"
     th:alt="'Foto de ' + ${mascota.nombre} + ', ' + ${mascota.especie}"
     class="w-48 h-48 rounded-2xl object-cover shadow-md">
```
Para imágenes puramente decorativas (fondos, íconos sin significado), usa `alt=""` (vacío pero presente) para que los lectores de pantalla las ignoren correctamente en vez de leer la URL.

### 5.2 Mensajes de error específicos por campo

**Problema:** el único lugar con mensajes de error concretos es el login (`"Credenciales inválidas"`). En el resto de formularios, si algo falla, la app redirige o cae en el manejador genérico de excepciones sin indicar qué corregir.

**Corrección:** ya cubierta en el punto 4.4 (`@Valid` + `th:errors`) — extiende ese mismo patrón a `registro.html`, `editar-perfil.html` y `admin-formulario-mascota.html`.

### 5.3 Sin feedback visual tras acciones administrativas

**Problema:** ligado al punto 4.3 — el admin guarda/edita/borra y solo ve la lista de nuevo, sin ninguna señal de que la acción se ejecutó correctamente.

**Corrección:** ya detallada en el punto 4.3 (bloque de mensaje verde con `th:if="${mensaje}"`). Aplícalo de forma consistente en las cuatro pantallas admin (`admin-mascotas`, `admin-duenos`, y las nuevas `admin-solicitudes` del punto 2.4).

### 5.4 El texto de confirmación de "eliminar" debe reflejar lo que realmente ocurre

**Problema:** ligado al punto 2.3 — el `confirm()` dice "eliminar" cuando en realidad se desactiva.

**Corrección:** ya incluida en el ejemplo del punto 2.3 (`"¿Desactivar esta mascota? No se eliminará..."`). Revisa también el `confirm()` de `admin-duenos.html` una vez apliques el soft-delete consistente del punto 2.6, y actualiza su texto de la misma forma.

---

## Resumen de prioridad sugerida

| Prioridad | Ítem | Sección |
|---|---|---|
| Alta | Eliminar entidades muertas (Cita/Veterinario/HistorialMedico) | 2.1 |
| Alta | Agregar teléfono/dirección al formulario de registro | 2.2 |
| Alta | Panel admin para solicitudes de adopción | 2.4 |
| Alta | Validación de formularios (`@Valid`) | 4.4 |
| Media | Reactivar mascotas desde el CRUD admin | 2.3 |
| Media | Consistencia de borrado Dueño/Mascota | 2.6 |
| Media | Enums para `estado`, `categoria`, `rol` | 3.2 |
| Media | Mensajes flash de éxito | 4.3 |
| Media | Accesibilidad (`alt` en imágenes) | 5.1 |
| Baja | Paginación y buscador en listados admin | 4.1, 4.2 |
| Baja | Auditoría (`fechaCreacion`/`fechaActualizacion`) | 3.7 |
| Baja | Normalizar `vacunas` en tabla aparte | 3.6 |
| Baja | Carrito de compras con backend real (o retirar el botón) | 2.5 |
| Baja | Índices explícitos | 3.5 |
