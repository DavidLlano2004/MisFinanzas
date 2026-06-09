# MisFinanzas

App Android en Kotlin para gestión de finanzas personales — construida capítulo a capítulo como material de curso.

## Guía del curso

| # | Capítulo | Código |
|---|----------|--------|
| 0 | [Configuración del entorno](docs/00_configuracion_entorno.md) | — |
| 1 | [Proyecto base: Hola Mundo](docs/01_proyecto_base.md) | `MainActivity.kt`, `activity_main.xml` |
| 2 | [Kotlin básico: variables, funciones, lógica](docs/02_kotlin_basico.md) | Kotlin puro |
| 3 | [Layouts y vistas: diseñando la interfaz](docs/03_layouts_vistas.md) | `activity_main.xml`, `colors.xml` |
| 4 | [Kotlin intermedio: clases, data classes, enums](docs/04_kotlin_intermedio.md) | `Categoria.kt`, `Transaccion.kt` |
| 5 | [Listas con RecyclerView](docs/05_listas_recyclerview.md) | `TransaccionAdapter.kt`, `item_transaccion.xml` |
| 6 | [Navegación entre pantallas](docs/06_navegacion.md) | `AgregarActivity.kt`, `activity_agregar.xml` |
| 7 | [Ciclo de vida y ViewModel](docs/07_ciclo_vida_viewmodel.md) | `MainViewModel.kt` |
| 8 | [Persistencia con Room](docs/08_persistencia_room.md) | `TransaccionDao.kt`, `AppDatabase.kt` |
| 9 | [Formularios y validación](docs/09_formularios_validacion.md) | `AgregarActivity.kt` (mejorado) |
| 10 | [Fragments y Bottom Navigation](docs/10_fragments_navegacion.md) | `InicioFragment.kt`, `TransaccionesFragment.kt`, `EstadisticasFragment.kt` |
| 11 | [Consumo de APIs con Retrofit](docs/11_retrofit_api.md) | `api/ApiService.kt`, `api/RetrofitClient.kt` |
| 12 | [Arquitectura MVVM](docs/12_arquitectura_mvvm.md) | `TransaccionRepository.kt`, `Extensions.kt` |
| 13 | [Kotlin avanzado](docs/13_kotlin_avanzado.md) | Coroutines, sealed classes, lambdas |
| 14 | [Proyecto final](docs/14_proyecto_final.md) | App completa |

## Estado actual de la app (Capítulo 14 — completa)

- **3 pestañas** con BottomNavigationView: Inicio, Movimientos, Estadísticas
- **Inicio:** tarjeta de balance con gradiente, tasa de ahorro, últimas 5 transacciones
- **Movimientos:** lista completa con swipe para eliminar + Snackbar de deshacer
- **Agregar:** formulario con validación en tiempo real, toggle ingreso/gasto, spinner de categorías
- **Estadísticas:** top 3 categorías de gasto + tasas de cambio en tiempo real (Retrofit)
- **Persistencia:** Room (SQLite) con datos que sobreviven a cierres de la app
- **Arquitectura:** MVVM + Repository pattern
- **Coroutines** para operaciones de red y base de datos

## Stack técnico

| Componente | Tecnología | Versión |
|------------|-----------|---------|
| Lenguaje | Kotlin | 1.9.24 |
| Build system | Gradle (KTS) | 8.7 |
| Android Gradle Plugin | AGP | 8.5.2 |
| UI components | Material Design 3 | 1.12.0 |
| Base de datos | Room + KSP | 2.6.1 |
| HTTP client | Retrofit + Gson | 2.9.0 |
| ViewModel / LiveData | Lifecycle KTX | 2.7.0 |
| Coroutines | kotlinx-coroutines | 1.7.3 |
| Annotation processor | KSP | 1.9.24-1.0.20 |
