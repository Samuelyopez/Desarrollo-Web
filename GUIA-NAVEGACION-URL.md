# Guía: navegar DogTor usando la URL

En DogTor **no hay sesión ni guards**: el portal en el que estás y la cédula de quien lo usa salen de la URL
(`utils/portal.ts`). Por eso puedes saltar a cualquier pantalla escribiendo su ruta en la barra del navegador,
sin pasar por el login. También funciona al recargar (F5) o pegar un enlace.

Base: `http://localhost:4200` (con `ng serve` corriendo y la API levantada).

## 1. La idea clave: tres portales

| Portal | Prefijo de la URL | Quién es |
|---|---|---|
| Cliente (dueño) | `/cliente/<cédula del dueño>` | El dueño cuya cédula está en la URL |
| Veterinario | `/vet/<cédula del veterinario>` | El veterinario cuya cédula está en la URL |
| Administrador | `/admin` | No lleva cédula |

**Para "cambiar de usuario" solo cambias el prefijo.** No hace falta cerrar sesión ni volver al login.

## 2. Datos de prueba (DataLoader)

| Rol | Cédulas |
|---|---|
| Administrador | `900000001`, `900000002` (no se usan en la URL) |
| Veterinarios | `800000001` Andrés, `800000002` Laura, `800000003` Camilo, `800000004` Paula, `800000005` Pedro (inactivo) |
| Dueños | `1000000001` Juan Pérez … `1000000010` Valentina Jiménez |

Mascotas con tratamientos de ejemplo: Max y Luna (Juan, `1000000001`), Rocky y Bella (María, `1000000002`),
Toby (Carlos, `1000000003`), Felix y Simba (Ana, `1000000004`), Buddy (Andrés, `1000000007`).

> Los **ids de mascota son generados** por la base de datos. Para saber el id, abre la tabla de mascotas,
> entra al detalle y mira el número al final de la URL (`.../mascota/3`).

## 3. Mapa completo de rutas

### Portal administrador

| Pantalla | URL |
|---|---|
| Inicio del portal | `/admin` |
| Dashboard (KPIs) | `/admin/dashboard` |
| Tabla de veterinarios | `/admin/veterinarios` |
| Crear veterinario | `/admin/veterinario/new` |
| Editar veterinario | `/admin/veterinario/update/<cédula vet>` |

### Portal veterinario (ej. `/vet/800000001`)

| Pantalla | URL |
|---|---|
| Inicio del portal | `/vet/800000001` |
| Tabla de dueños | `/vet/800000001/duenos` |
| Detalle de un dueño | `/vet/800000001/dueno/<cédula dueño>` |
| Crear dueño | `/vet/800000001/dueno/new` |
| Editar dueño | `/vet/800000001/dueno/update/<cédula dueño>` |
| Tabla de mascotas | `/vet/800000001/mascotas` |
| Detalle de una mascota | `/vet/800000001/mascota/<id>` |
| Crear mascota | `/vet/800000001/mascota/new` |
| Editar mascota | `/vet/800000001/mascota/update/<id>` |
| Dar tratamiento | `/vet/800000001/tratamiento/new` |
| Mis pacientes | `/vet/800000001/pacientes` |

### Portal cliente (ej. `/cliente/1000000001`)

| Pantalla | URL |
|---|---|
| Inicio del portal | `/cliente/1000000001` |
| Mis mascotas | `/cliente/1000000001/mascotas` |
| Detalle de mi mascota + tratamientos | `/cliente/1000000001/mascota/<id>` |

Cualquier ruta que no exista (`**`) redirige a la landing `/`.

## 4. Recetas: pasar de un portal a otro

### De admin a ver un dueño
El portal admin no tiene pantallas de dueños (los gestiona el veterinario). Tienes dos opciones:

1. **Como lo vería un veterinario** (datos del dueño y sus mascotas):
   ```
   /admin  →  /vet/800000001/dueno/1000000001
   ```
2. **Como lo ve el propio dueño** (su portal):
   ```
   /admin  →  /cliente/1000000001
   ```

### De admin a ver una mascota
1. Abre `/vet/800000001/mascotas` y busca la mascota para conocer su id.
2. Detalle (vista veterinario): `/vet/800000001/mascota/<id>`
3. Vista del dueño, con historial de tratamientos: `/cliente/<cédula del dueño>/mascota/<id>`

> En el portal cliente la API responde **404 si la mascota no es del dueño de la URL**. Ej.: si Max es de Juan,
> `/cliente/1000000002/mascota/<id de Max>` falla porque `1000000002` es María.

### De un veterinario a otro
Cambia solo la cédula del prefijo, el resto de la ruta se mantiene:
```
/vet/800000001/pacientes  →  /vet/800000003/pacientes
```
Así ves "Mis pacientes" de Camilo en vez de Andrés.

### De un dueño a otro
```
/cliente/1000000001/mascotas  →  /cliente/1000000004/mascotas
```

### De veterinario a admin (y volver)
```
/vet/800000001/duenos  →  /admin/veterinarios  →  /vet/800000001
```

### Probar el veterinario inactivo
Pedro (`800000005`) no puede iniciar sesión, pero sí puedes abrir su portal por URL:
```
/vet/800000005/tratamiento/new
```
La pantalla carga, pero al guardar la **API responde 409**: las reglas se validan en el servidor, no en la URL.

## 5. Recorrido sugerido para la demo

1. `/admin/dashboard` → KPIs del negocio.
2. `/admin/veterinarios` → ver que Pedro está inactivo.
3. `/vet/800000001/duenos` → abrir a Juan (`/vet/800000001/dueno/1000000001`).
4. Desde el detalle de Juan, entrar a Max → copiar el id de la URL.
5. `/vet/800000001/tratamiento/new` → darle un tratamiento a Max.
6. `/vet/800000001/pacientes` → Max aparece en mis pacientes.
7. `/cliente/1000000001/mascota/<id de Max>` → Juan ve el tratamiento nuevo.

## 6. Ten en cuenta

- Los enlaces de la navbar y de cada pantalla se arman desde el prefijo actual (`portal.base`), así que una vez
  dentro de un portal puedes seguir navegando con clics.
- Que se pueda abrir cualquier URL es una **decisión del proyecto** (sin guards). La limitación conocida es que la
  API no sabe quién la llama; la mejora natural es Spring Security + JWT con `@PreAuthorize` por rol.
