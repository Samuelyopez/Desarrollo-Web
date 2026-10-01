---
name: verificar-cambio
description: Corre el gate completo de las correcciones de DogTor Angular (build sin avisos de presupuesto, todos los gates estáticos de los pasos 1-9 y lo que no debe cambiar frente a dogtor-base). Úsalo antes de marcar una tarea como done, después de un rollback, o cuando pidan "verificar", "correr el gate" o "¿está todo en verde?".
---

# Verificar cambio

## Cuándo usarlo

- Antes de marcar como `done` cualquier tarea de `blueprints/correcciones-profe/tasks.json`.
- Después de un rollback (`git restore --source=dogtor-sNN ... -- src`), para confirmar el estado.
- Cuando alguien pregunte si las correcciones del profesor siguen en verde.

## Pasos

1. Ubícate en `dogtor-angular/` (la carpeta que contiene `package.json`).
2. Lee `blueprints/correcciones-profe/tasks.json` y toma el arreglo `verify` de la tarea `E3-T5`:
   es exactamente el gate global de §20.1 del blueprint (build + los verify de E1-T1…E3-T4 +
   comprobaciones finales).
3. Ejecuta cada comando del arreglo, en orden, uno por uno. Cada uno debe terminar con exit 0.
   Antes de que exista la etiqueta `dogtor-s09`, la última línea (que comprueba las etiquetas)
   falla por diseño: en ese caso corre solo los `verify` de las tareas ya marcadas `done` más la
   tarea actual.
4. Si un comando falla, anota cuál y qué imprimió; vuelve a la tarea dueña de ese comando y
   corrige el código allí.

## Verify

```bash
npx ng build   # expect: exit 0
test -d src/app && test -z "$(grep -rn duenoId src/app)"   # expect: exit 0 (desde el paso 2)
test -d src/app && test -z "$(grep -rni whatsapp src/app)"   # expect: exit 0 (desde el paso 6)
```

## No hacer

- No editar ni saltarse un comando `verify` para que pase.
- No "arreglar" con `git reset --hard`, `git stash` ni `git add -A` sin pathspec: el repo padre
  tiene cambios sin commit de `Proyecto-Veterinaria-2.0/`.
- No instalar paquetes ni agregar tests/linters para verificar.
