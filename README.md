# doit — gestor de tareas

Aplicación Android de gestión de tareas con base de datos **SQLite** local y autenticación de usuarios.

**Curso:** Diseño y Desarrollo de Aplicaciones Móviles – Seminario (IA 501)
**Institución:** SENATI – Escuela de Tecnologías de la Información
**Instructor:** Giancarlos Barboza N.
**Diseño:** [Figma](https://www.figma.com/design/0gkxdySCwSJRM8RrRHpeqv)

> El nombre visible de la app es **doit**. El paquete sigue siendo `com.example.apptareas` y **no debe cambiarse**: es el que está registrado en Firebase junto a las huellas SHA-1. Cambiarlo rompería el inicio de sesión con Google.

---

## Cómo compilar y ejecutar

### Requisitos

| | Versión |
|---|---|
| Android Gradle Plugin | 8.6.1 |
| Gradle | 8.14.3 |
| JDK | **17 a 24** (ver aviso abajo) |
| `compileSdk` / `targetSdk` | 35 |
| `minSdk` | 28 |

### Aviso importante sobre el JDK

Gradle 8.14.3 **no soporta Java 25**. Si Android Studio arranca con su JBR 25 incluido, la sincronización falla con un error críptico que solo dice `25.0.2`.

Si te pasa, en Android Studio: **Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK** y elige un JDK **17 a 24**.

### Pasos

```bash
git clone https://github.com/Dgirto/appTareas.git
cd appTareas
./gradlew assembleDebug      # genera el APK de depuración
./gradlew installDebug       # lo instala en el dispositivo conectado
./gradlew test               # ejecuta las pruebas unitarias
```

El APK queda en `app/build/outputs/apk/debug/app-debug.apk`.

---

## Credenciales de prueba

La app crea una **cuenta de demostración con cinco tareas de ejemplo** la primera vez que se abre, solo en compilaciones de depuración. Sirve para probar la app y hacer capturas sin registrar nada a mano.

| Correo | Contraseña |
|---|---|
| `demo@doit.pe` | `demo1234` |

Las cinco tareas cubren los tres estados (pendiente, en progreso y completada), con fechas de vencimiento y usuarios asignados, para que los filtros y el buscador se puedan probar de inmediato.

También puedes entrar con **«Continuar como invitado»**, sin credenciales, o **registrar tu propia cuenta** desde «Regístrate».

> La siembra solo ocurre si la cuenta `demo@doit.pe` no existe todavía, así que nunca pisa datos reales ni se repite. En un APK de release no se ejecuta.

---

## Inicio de sesión con Google

**Cualquier cuenta de Google sirve.** No hay lista de correos autorizados: la app no restringe quién puede entrar, y crea la cuenta local automáticamente la primera vez.

Lo que sí está restringido es **desde qué compilación** se puede pedir el token. Google verifica la app por su paquete y la huella SHA-1 del keystore que la firmó.

### Cada integrante debe registrar su propia SHA-1

Si no lo haces, el botón te fallará **solo a ti**, con un error genérico:

```bash
./gradlew signingReport      # copia la SHA1 de la variante "debug"
```

Y esa huella se añade en la consola de Firebase → **Configuración del proyecto → Tus apps → Agregar huella digital**. Firebase admite varias, una por equipo. Si vais a presentar desde el PC del laboratorio, esa máquina también necesita la suya.

### `google-services.json`

**No está en el repositorio** y no debe subirse: se comparte por interno del equipo. Va en `app/google-services.json`.

El ID de cliente web sí está versionado, en `res/values/auth_config.xml`. No es un secreto — viaja dentro de cualquier APK publicado y solo identifica el proyecto —, y tenerlo en el repo permite que cualquiera compile sin necesitar el `google-services.json`.

---

## Estructura del proyecto

```
app/src/main/java/com/example/apptareas/
├── ui/          Activities, adapter del RecyclerView, diálogos
├── model/       Tarea, Usuario, EstadoTarea
├── data/        SQLite: DatabaseHelper, TareaContract, TareaDao, UsuarioDao
├── auth/        AuthManager, SesionManager, GoogleAuthClient
└── util/        Validaciones, FechaUtils, Resultado
```

Las cinco pantallas son: inicio de sesión, registro, lista de tareas, detalle de tarea y formulario de crear/editar.

## Base de datos

Archivo `tareas.db`, versión 1. Dos tablas:

- **`usuarios`** — `_id`, `nombre`, `correo` (único), `password_hash`, `proveedor` (`local` | `google`), `google_id`, `foto_url`, `fecha_registro`
- **`tareas`** — `_id`, `titulo`, `descripcion`, `estado`, `fecha_vencimiento`, `fecha_creacion`, `usuario_asignado`, `usuario_id` → `usuarios(_id)` con `ON DELETE CASCADE`

Convenciones:

- Las fechas se guardan en ISO `yyyy-MM-dd` (ordenable en SQL) y se muestran como `dd/MM/yyyy`.
- El estado se guarda en minúsculas: `pendiente`, `en progreso`, `completada`.
- `fecha_creacion` la asigna el DAO al insertar, nunca el usuario.
- `usuario_asignado` es texto libre; `usuario_id` es la relación real con la cuenta que creó la tarea. Son cosas distintas.
- Las contraseñas se guardan como **SHA-256 con salt**, nunca en claro.
- Las claves foráneas están activas (`PRAGMA foreign_keys=ON`), así que cada tarea debe pertenecer a un usuario que exista.

---

## Equipo

Reparto según los commits que hay en el repositorio:

| Integrante | Módulos | Rama |
|---|---|---|
| Dorian Girón (`Dgirto`) | `ui/` completo: Activities, adapter, layouts, tema, iconos, navegación. Y `auth/` | `feature/giron` |
| Piero Llamocca (`Piero200314`) | `model/` y `data/`: esquema SQLite, `DatabaseHelper`, `TareaContract`, `TareaDao`, `UsuarioDao` | `feature/piero` (ya integrada) |
| Guillermo Pereyra (`SCAREDbelike`) | `util/`: `Validaciones`, `FechaUtils`, `Resultado`, y sus pruebas unitarias | `feature/pereyra` |
| Matías Meléndez (`Magidark00`) | Modelado de la base de datos y ajuste del toolchain de Gradle | `carlos` |
| Daniel León (`Weakdlt`) | Autenticación | `feature/leon` — **sin integrar** |

> **Nota de honestidad sobre `auth/`.** Según el reparto original, el módulo de autenticación le correspondía a Daniel León. Su implementación existe en la rama `feature/leon`, pero usa **Room** mientras el resto del proyecto usa `SQLiteOpenHelper` a mano, y crearía una segunda tabla de usuarios (`users`) junto a la que ya existe (`usuarios`). Ante eso, el `auth/` que está en `main` lo escribió Dorian siguiendo el contrato acordado y apoyándose en el `UsuarioDao` de Piero. Queda pendiente que el equipo decida qué hacer con las dos implementaciones.

### B4 — Reglas de negocio e integración

Se implementaron las utilidades correspondientes a las reglas de negocio:

- Validación del título obligatorio.
- Validación de fechas de creación y vencimiento.
- Validación de transiciones de estado.
- Conversión de fechas entre formato ISO y formato de usuario.
- Clase `Resultado` para gestionar respuestas exitosas y errores.
- Ajuste de dependencias para mantener la compatibilidad del proyecto.

---

## Pendiente

- Probar el inicio de sesión con Google en un dispositivo real. En el emulador no se puede: no hay ninguna cuenta de Google configurada.
- Que cada integrante registre su huella SHA-1 en Firebase.
- La rama `feature/leon` contiene una implementación alternativa de autenticación con **Room** que no se llegó a integrar: choca con el `SQLiteOpenHelper` que usa el resto del proyecto y crearía una segunda tabla de usuarios.
