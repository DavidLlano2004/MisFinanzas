# Guía rápida de estudio — MisFinanzas
### "¿Dónde queda X?" — referencia por funcionalidad con líneas exactas

---

## OPERACIONES CRUD

### CREAR una transacción — flujo completo

| Paso | Archivo | Línea | Qué hace |
|------|---------|-------|----------|
| 1. Botón abrir formulario | [TransaccionesFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionesFragment.kt#L66-L68) | 66-68 | `lanzarAgregar.launch(Intent(...))` |
| 2. Formulario se abre | [AgregarActivity.kt](../app/src/main/java/com/ejemplo/misfinanzas/AgregarActivity.kt#L18-L26) | 18-26 | `onCreate` con ViewBinding |
| 3. Validar en tiempo real | [AgregarActivity.kt](../app/src/main/java/com/ejemplo/misfinanzas/AgregarActivity.kt#L69-L99) | 69-99 | `doAfterTextChanged` → `validarFormulario()` |
| 4. Guardar y devolver | [AgregarActivity.kt](../app/src/main/java/com/ejemplo/misfinanzas/AgregarActivity.kt#L101-L120) | 101-120 | `setResult(RESULT_OK)` + `finish()` |
| 5. Recibir resultado | [TransaccionesFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionesFragment.kt#L27-L37) | 27-37 | `registerForActivityResult` |
| 6. Llamar al ViewModel | [TransaccionesFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionesFragment.kt#L33) | 33 | `viewModel.agregarTransaccion(nueva)` |
| 7. ViewModel → Repository | [MainViewModel.kt](../app/src/main/java/com/ejemplo/misfinanzas/MainViewModel.kt#L40-L42) | 40-42 | `viewModelScope.launch { repository.insertar(t) }` |
| 8. Repository → DAO | [TransaccionRepository.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionRepository.kt#L17) | 17 | `suspend fun insertar(t) = dao.insertar(t)` |
| 9. DAO → SQLite | [TransaccionDao.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionDao.kt#L25-L26) | 25-26 | `@Insert suspend fun insertar(...)` |
| 10. UI se actualiza sola | [TransaccionesFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionesFragment.kt#L53-L62) | 53-62 | LiveData observable → nuevo adapter |

---

### LEER / MOSTRAR transacciones

| Qué muestra | Archivo | Línea | Detalle |
|-------------|---------|-------|---------|
| Lista completa | [TransaccionesFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionesFragment.kt#L53-L62) | 53-62 | Observa `viewModel.transacciones` |
| Últimas 5 (Inicio) | [InicioFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/InicioFragment.kt#L49-L58) | 49-58 | `lista.take(5)` → adapter |
| Balance total | [InicioFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/InicioFragment.kt#L37-L39) | 37-39 | Observa `viewModel.balance` |
| Ingresos | [InicioFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/InicioFragment.kt#L41-L43) | 41-43 | Observa `viewModel.ingresos` |
| Gastos | [InicioFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/InicioFragment.kt#L45-L47) | 45-47 | Observa `viewModel.gastos` |
| Tasa de ahorro | [InicioFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/InicioFragment.kt#L50-L54) | 50-54 | `(balance / ingresos) * 100` |
| Consulta SQL real | [TransaccionDao.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionDao.kt#L10-L23) | 10-23 | 5 `@Query` con `LiveData<>` |

---

### ELIMINAR una transacción

| Paso | Archivo | Línea | Qué hace |
|------|---------|-------|----------|
| Swipe izquierda detectado | [TransaccionesFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionesFragment.kt#L79-L89) | 79-89 | `onSwiped` del `ItemTouchHelper` |
| Llamar al ViewModel | [TransaccionesFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionesFragment.kt#L84) | 84 | `viewModel.eliminarTransaccion(transaccion)` |
| ViewModel → Repository | [MainViewModel.kt](../app/src/main/java/com/ejemplo/misfinanzas/MainViewModel.kt#L44-L46) | 44-46 | `viewModelScope.launch { repository.eliminar(t) }` |
| DAO borra de SQLite | [TransaccionDao.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionDao.kt#L34-L35) | 34-35 | `@Delete suspend fun eliminar(...)` |
| Snackbar con deshacer | [TransaccionesFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionesFragment.kt#L86-L88) | 86-88 | `Snackbar.make(...).setAction("Deshacer")` |
| Deshacer re-inserta | [TransaccionesFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionesFragment.kt#L87) | 87 | `viewModel.agregarTransaccion(transaccion)` |

---

### ESTADÍSTICAS

| Qué calcula | Archivo | Línea | Detalle |
|-------------|---------|-------|---------|
| Top 3 categorías | [EstadisticasFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/EstadisticasFragment.kt#L69-L74) | 69-74 | `groupBy → mapValues → sortedByDescending → take(3)` |
| Promedio de gasto | [EstadisticasFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/EstadisticasFragment.kt#L95-L97) | 95-97 | `sumOf / gastos.size` |
| Mayor ingreso | [EstadisticasFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/EstadisticasFragment.kt#L86-L89) | 86-89 | `maxByOrNull { it.monto }` |

---

## NAVEGACIÓN

### Cómo se navega entre pestañas

| Elemento | Archivo | Línea | Detalle |
|----------|---------|-------|---------|
| Layout con BottomNav | [activity_main.xml](../app/src/main/res/layout/activity_main.xml) | — | `FrameLayout(fragmentContainer)` + `BottomNavigationView` |
| Menú de las pestañas | [bottom_menu.xml](../app/src/main/res/menu/bottom_menu.xml) | — | 3 `<item>`: nav_inicio, nav_transacciones, nav_estadisticas |
| Listener de pestañas | [MainActivity.kt](../app/src/main/java/com/ejemplo/misfinanzas/MainActivity.kt#L29-L36) | 29-36 | `setOnItemSelectedListener { when(item.itemId) }` |
| Cargar un Fragment | [MainActivity.kt](../app/src/main/java/com/ejemplo/misfinanzas/MainActivity.kt#L39-L43) | 39-43 | `supportFragmentManager.beginTransaction().replace().commit()` |
| Fragment inicial | [MainActivity.kt](../app/src/main/java/com/ejemplo/misfinanzas/MainActivity.kt#L19-L21) | 19-21 | `if (savedInstanceState == null)` → `InicioFragment()` |

### Cómo se navega a la pantalla de agregar

| Elemento | Archivo | Línea |
|----------|---------|-------|
| Lanzar AgregarActivity | [TransaccionesFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionesFragment.kt#L66-L68) | 66-68 |
| Registrar en Manifest | [AndroidManifest.xml](../app/src/main/AndroidManifest.xml) | — |
| Recibir resultado | [TransaccionesFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionesFragment.kt#L27-L37) | 27-37 |
| Devolver resultado | [AgregarActivity.kt](../app/src/main/java/com/ejemplo/misfinanzas/AgregarActivity.kt#L116-L119) | 116-119 |

---

## DATOS / MODELO

### Dónde está definida la estructura de datos

| Concepto | Archivo | Línea | Detalle |
|----------|---------|-------|---------|
| Modelo `Transaccion` | [modelo/Transaccion.kt](../app/src/main/java/com/ejemplo/misfinanzas/modelo/Transaccion.kt#L8-L16) | 8-16 | `@Entity data class` con 5 campos |
| Clave primaria auto | [modelo/Transaccion.kt](../app/src/main/java/com/ejemplo/misfinanzas/modelo/Transaccion.kt#L10-L11) | 10-11 | `@PrimaryKey(autoGenerate = true) val id: Int = 0` |
| Por qué `categoriaNombre` y no `categoria` | [modelo/Transaccion.kt](../app/src/main/java/com/ejemplo/misfinanzas/modelo/Transaccion.kt#L14) | 14 | Room no puede guardar enums; se guarda `.name` del enum |
| Convertir nombre → enum | [modelo/Transaccion.kt](../app/src/main/java/com/ejemplo/misfinanzas/modelo/Transaccion.kt#L20-L25) | 20-25 | `val categoria get() = Categoria.valueOf(categoriaNombre)` |
| Saber si es ingreso | [modelo/Transaccion.kt](../app/src/main/java/com/ejemplo/misfinanzas/modelo/Transaccion.kt#L18) | 18 | `val esIngreso: Boolean get() = monto > 0` |
| Formatear monto | [modelo/Transaccion.kt](../app/src/main/java/com/ejemplo/misfinanzas/modelo/Transaccion.kt#L27-L30) | 27-30 | `montoFormateado()` — signo + formato |
| Datos de prueba | [modelo/Transaccion.kt](../app/src/main/java/com/ejemplo/misfinanzas/modelo/Transaccion.kt#L33-L43) | 33-43 | `companion object { fun datosDePrueba() }` |
| Enum `Categoria` | [modelo/Categoria.kt](../app/src/main/java/com/ejemplo/misfinanzas/modelo/Categoria.kt#L3-L19) | 3-19 | 9 valores con emoji, etiqueta, colorFondoHex |
| Cuáles son ingresos | [modelo/Categoria.kt](../app/src/main/java/com/ejemplo/misfinanzas/modelo/Categoria.kt#L18) | 18 | `fun esIngreso() = this == SALARIO \|\| this == FREELANCE` |

---

## ROOM (BASE DE DATOS)

### Cómo funciona la persistencia

| Concepto | Archivo | Línea | Detalle |
|----------|---------|-------|---------|
| Marcar tabla de BD | [modelo/Transaccion.kt](../app/src/main/java/com/ejemplo/misfinanzas/modelo/Transaccion.kt#L8) | 8 | `@Entity(tableName = "transacciones")` |
| Todas las consultas SQL | [TransaccionDao.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionDao.kt) | 1-39 | 5 `@Query`, `@Insert`, `@Update`, `@Delete` |
| SELECT con LiveData | [TransaccionDao.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionDao.kt#L10-L11) | 10-11 | `fun obtenerTodas(): LiveData<List<Transaccion>>` |
| Crear la BD (singleton) | [AppDatabase.kt](../app/src/main/java/com/ejemplo/misfinanzas/AppDatabase.kt#L16-L28) | 16-28 | `Room.databaseBuilder(...)` — patrón Singleton |
| `@Volatile` para thread safety | [AppDatabase.kt](../app/src/main/java/com/ejemplo/misfinanzas/AppDatabase.kt#L13-L14) | 13-14 | `@Volatile private var INSTANCE: AppDatabase? = null` |
| Conexión en el ViewModel | [MainViewModel.kt](../app/src/main/java/com/ejemplo/misfinanzas/MainViewModel.kt#L31) | 31 | `AppDatabase.obtenerInstancia(application).transaccionDao()` |
| Insertar en coroutine | [TransaccionDao.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionDao.kt#L25-L26) | 25-26 | `suspend fun insertar(...)` — debe ir en hilo IO |
| Plugin que procesa @Dao | [app/build.gradle.kts](../app/build.gradle.kts) | 3 | `id("com.google.devtools.ksp")` — genera código SQL |

---

## VIEWMODEL Y LIVEDATA

### Por qué no se usan variables normales en la Activity

| Concepto | Archivo | Línea | Detalle |
|----------|---------|-------|---------|
| Obtener ViewModel | [MainActivity.kt](../app/src/main/java/com/ejemplo/misfinanzas/MainActivity.kt#L12) | 12 | `private val viewModel: MainViewModel by viewModels()` |
| Compartir entre Fragments | [InicioFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/InicioFragment.kt#L20) | 20 | `by activityViewModels()` — misma instancia |
| Observar en Activity | [MainActivity.kt](../app/src/main/java/com/ejemplo/misfinanzas/MainActivity.kt#L23-L27) | 23-27 | `viewModel.transacciones.observe(this) { lista -> }` |
| Observar en Fragment | [InicioFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/InicioFragment.kt#L37-L39) | 37-39 | `observe(viewLifecycleOwner)` — NO usar `this` |
| LiveData privado vs público | [MainViewModel.kt](../app/src/main/java/com/ejemplo/misfinanzas/MainViewModel.kt#L27-L28) | 27-28 | `_tasasCambio` MutableLiveData (privado) vs `tasasCambio` LiveData (público) |
| Coroutine en ViewModel | [MainViewModel.kt](../app/src/main/java/com/ejemplo/misfinanzas/MainViewModel.kt#L40-L42) | 40-42 | `viewModelScope.launch { ... }` — se cancela al destruirse |
| `AndroidViewModel` | [MainViewModel.kt](../app/src/main/java/com/ejemplo/misfinanzas/MainViewModel.kt#L17) | 17 | `class MainViewModel(application: Application) : AndroidViewModel(application)` — necesita contexto |

---

## RETROFIT (API)

### Dónde se configura y usa la llamada de red

| Concepto | Archivo | Línea | Detalle |
|----------|---------|-------|---------|
| URL base de la API | [api/RetrofitClient.kt](../app/src/main/java/com/ejemplo/misfinanzas/api/RetrofitClient.kt#L8) | 8 | `BASE_URL = "https://api.exchangerate-api.com/v4/"` |
| Singleton del cliente | [api/RetrofitClient.kt](../app/src/main/java/com/ejemplo/misfinanzas/api/RetrofitClient.kt#L6-L17) | 6-17 | `object RetrofitClient { val apiService by lazy { ... } }` |
| Definir endpoint | [api/ApiService.kt](../app/src/main/java/com/ejemplo/misfinanzas/api/ApiService.kt#L8-L11) | 8-11 | `@GET("latest") suspend fun obtenerTasas(@Query("base") ...)` |
| Modelo de la respuesta JSON | [api/TasaCambioResponse.kt](../app/src/main/java/com/ejemplo/misfinanzas/api/TasaCambioResponse.kt) | 1-5 | `data class TasaCambioResponse(val base, val rates: Map<String, Double>)` |
| Estados posibles | [api/ResultadoApi.kt](../app/src/main/java/com/ejemplo/misfinanzas/api/ResultadoApi.kt#L3-L7) | 3-7 | `sealed class`: `Cargando`, `Exito(datos)`, `Error(mensaje)` |
| Llamada con manejo de error | [MainViewModel.kt](../app/src/main/java/com/ejemplo/misfinanzas/MainViewModel.kt#L56-L72) | 56-72 | `try/catch` con `withContext(Dispatchers.IO)` |
| Mostrar en la UI | [EstadisticasFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/EstadisticasFragment.kt#L36-L63) | 36-63 | `when (resultado)` → Cargando / Exito / Error |
| Permiso de internet | [AndroidManifest.xml](../app/src/main/AndroidManifest.xml) | 4 | `<uses-permission INTERNET />` |

---

## FRAGMENTS Y BINDING

### El patrón de binding seguro en Fragments

| Concepto | Archivo | Línea | Detalle |
|----------|---------|-------|---------|
| Declarar `_binding` nullable | [InicioFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/InicioFragment.kt#L17-L18) | 17-18 | `private var _binding: ...? = null` + `private val binding get() = _binding!!` |
| Inflar el layout | [InicioFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/InicioFragment.kt#L22-L29) | 22-29 | `onCreateView` → `FragmentInicioBinding.inflate(inflater, container, false)` |
| Configurar vistas | [InicioFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/InicioFragment.kt#L31-L58) | 31-58 | `onViewCreated` — observers y listeners aquí |
| Evitar memory leak | [InicioFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/InicioFragment.kt#L66-L69) | 66-69 | `onDestroyView { _binding = null }` |

---

## RECYCLERVIEW

### Cómo funciona la lista

| Concepto | Archivo | Línea | Detalle |
|----------|---------|-------|---------|
| Clase Adapter | [TransaccionAdapter.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionAdapter.kt#L11-L14) | 11-14 | Recibe lista + lambda `onItemClick` |
| ViewHolder | [TransaccionAdapter.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionAdapter.kt#L16-L17) | 16-17 | `inner class ViewHolder(val binding: ItemTransaccionBinding)` |
| Inflar el item | [TransaccionAdapter.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionAdapter.kt#L19-L24) | 19-24 | `onCreateViewHolder` → infla `item_transaccion.xml` |
| Poner datos | [TransaccionAdapter.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionAdapter.kt#L26-L50) | 26-50 | `onBindViewHolder` — texto, colores, click |
| Color del ícono por categoría | [TransaccionAdapter.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionAdapter.kt#L46-L47) | 46-47 | `.mutate()` para no compartir el drawable entre items |
| Cuántos items | [TransaccionAdapter.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionAdapter.kt#L52) | 52 | `getItemCount(): Int = transacciones.size` |
| Conectar al RecyclerView | [TransaccionesFragment.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionesFragment.kt#L51-L61) | 51-61 | `layoutManager` + `adapter` asignados en observer |

---

## ARQUITECTURA MVVM

### El flujo completo de datos

```
UI (Fragment)  →  ViewModel  →  Repository  →  DAO  →  SQLite
               ←  LiveData  ←              ←       ←
                                  Repository  →  Retrofit  →  API
```

| Capa | Archivo | Responsabilidad |
|------|---------|----------------|
| View | `InicioFragment`, `TransaccionesFragment`, `EstadisticasFragment` | Mostrar datos, capturar eventos |
| ViewModel | [MainViewModel.kt](../app/src/main/java/com/ejemplo/misfinanzas/MainViewModel.kt) | Lógica de presentación, LiveData, coroutines |
| Repository | [TransaccionRepository.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionRepository.kt) | Abstrae Room y Retrofit |
| DAO | [TransaccionDao.kt](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionDao.kt) | Consultas SQL sobre SQLite |
| API | [api/ApiService.kt](../app/src/main/java/com/ejemplo/misfinanzas/api/ApiService.kt) | Endpoints HTTP |

---

## EXTENSION FUNCTIONS

### Funciones de utilidad disponibles en toda la app

| Función | Archivo | Línea | Uso |
|---------|---------|-------|-----|
| `Double.formatearCOP()` | [Extensions.kt](../app/src/main/java/com/ejemplo/misfinanzas/Extensions.kt#L15-L16) | 15-16 | `balance.formatearCOP()` → `"$ 2,500,000"` |
| `View.mostrar()` | [Extensions.kt](../app/src/main/java/com/ejemplo/misfinanzas/Extensions.kt#L11) | 11 | `binding.progressBar.mostrar()` |
| `View.ocultar()` | [Extensions.kt](../app/src/main/java/com/ejemplo/misfinanzas/Extensions.kt#L12) | 12 | `binding.progressBar.ocultar()` |
| `Activity.mostrarToast()` | [Extensions.kt](../app/src/main/java/com/ejemplo/misfinanzas/Extensions.kt#L7-L9) | 7-9 | `mostrarToast("Guardado")` |

---

## PREGUNTAS TÍPICAS DE PROFESOR

| Pregunta | Respuesta directa | Archivo:Línea |
|----------|------------------|---------------|
| ¿Dónde se define la tabla de la BD? | `@Entity` en `Transaccion.kt` | [Transaccion.kt:8](../app/src/main/java/com/ejemplo/misfinanzas/modelo/Transaccion.kt#L8) |
| ¿Dónde se escriben las consultas SQL? | `TransaccionDao.kt`, interfaz con `@Query` | [TransaccionDao.kt:10](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionDao.kt#L10) |
| ¿Cómo se crea la base de datos? | `Room.databaseBuilder` en `AppDatabase.kt` | [AppDatabase.kt:18](../app/src/main/java/com/ejemplo/misfinanzas/AppDatabase.kt#L18) |
| ¿Dónde se conecta la BD con el ViewModel? | `init` de `MainViewModel.kt` | [MainViewModel.kt:31](../app/src/main/java/com/ejemplo/misfinanzas/MainViewModel.kt#L31) |
| ¿Por qué se usa `viewLifecycleOwner` en Fragments? | Para no observar cuando la vista no existe (evita crashes) | [InicioFragment.kt:37](../app/src/main/java/com/ejemplo/misfinanzas/InicioFragment.kt#L37) |
| ¿Por qué `_binding = null` en `onDestroyView`? | Para evitar memory leaks — el Fragment puede vivir sin vista | [InicioFragment.kt:68](../app/src/main/java/com/ejemplo/misfinanzas/InicioFragment.kt#L68) |
| ¿Cómo se pasan datos entre Activities? | `Serializable` + `putExtra` / `getSerializableExtra` | [AgregarActivity.kt:116-118](../app/src/main/java/com/ejemplo/misfinanzas/AgregarActivity.kt#L116) |
| ¿Dónde se registra `AgregarActivity`? | `AndroidManifest.xml` | [AndroidManifest.xml](../app/src/main/AndroidManifest.xml) |
| ¿Por qué `suspend` en el DAO? | Las operaciones de BD no pueden ir en el hilo principal | [TransaccionDao.kt:26](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionDao.kt#L26) |
| ¿Qué hace `viewModelScope.launch`? | Inicia coroutine ligada al ViewModel; se cancela al destruirse | [MainViewModel.kt:41](../app/src/main/java/com/ejemplo/misfinanzas/MainViewModel.kt#L41) |
| ¿Por qué `categoriaNombre` y no `categoria`? | Room solo guarda tipos primitivos, no enums | [Transaccion.kt:14](../app/src/main/java/com/ejemplo/misfinanzas/modelo/Transaccion.kt#L14) |
| ¿Qué hace `activityViewModels()`? | Accede al ViewModel de la Activity — todos los Fragments comparten el mismo | [InicioFragment.kt:20](../app/src/main/java/com/ejemplo/misfinanzas/InicioFragment.kt#L20) |
| ¿Dónde se define el endpoint de la API? | `ApiService.kt` con `@GET` | [ApiService.kt:8](../app/src/main/java/com/ejemplo/misfinanzas/api/ApiService.kt#L8) |
| ¿Qué es una `sealed class`? | Clase cerrada cuyos subtipos son conocidos (Cargando/Exito/Error) | [ResultadoApi.kt:3](../app/src/main/java/com/ejemplo/misfinanzas/api/ResultadoApi.kt#L3) |
| ¿Por qué `Dispatchers.IO` para la red? | Las llamadas de red bloquean el hilo; IO las ejecuta en segundo plano | [MainViewModel.kt:60](../app/src/main/java/com/ejemplo/misfinanzas/MainViewModel.kt#L60) |
| ¿Qué hace `mutate()` en el adapter? | Crea una copia del drawable para que cada item tenga su propio color | [TransaccionAdapter.kt:46](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionAdapter.kt#L46) |
| ¿Dónde se define la navegación por pestañas? | Menú `bottom_menu.xml` + listener en `MainActivity.kt` | [MainActivity.kt:29](../app/src/main/java/com/ejemplo/misfinanzas/MainActivity.kt#L29) |
| ¿Cómo se cambia de Fragment? | `.beginTransaction().replace().commit()` | [MainActivity.kt:40](../app/src/main/java/com/ejemplo/misfinanzas/MainActivity.kt#L40) |
| ¿Qué es el Repository? | Capa que abstrae de dónde vienen los datos (Room o API) | [TransaccionRepository.kt:8](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionRepository.kt#L8) |
| ¿Cómo funciona el swipe para eliminar? | `ItemTouchHelper.SimpleCallback` con `onSwiped` | [TransaccionesFragment.kt:72](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionesFragment.kt#L72) |
| ¿Dónde se valida el formulario? | `validarFormulario()` llamado desde `doAfterTextChanged` | [AgregarActivity.kt:75](../app/src/main/java/com/ejemplo/misfinanzas/AgregarActivity.kt#L75) |
| ¿Cómo se recibe el resultado de AgregarActivity? | `registerForActivityResult` declarado a nivel de clase | [TransaccionesFragment.kt:27](../app/src/main/java/com/ejemplo/misfinanzas/TransaccionesFragment.kt#L27) |

---

## MAPA DE ARCHIVOS COMPLETO

```
app/src/main/
├── AndroidManifest.xml          ← registra Activities + permiso INTERNET
├── java/com/ejemplo/misfinanzas/
│   ├── MainActivity.kt          ← BottomNav + carga Fragments
│   ├── MainViewModel.kt         ← lógica, LiveData, coroutines
│   ├── AgregarActivity.kt       ← formulario nueva transacción
│   ├── TransaccionDao.kt        ← consultas SQL (@Dao)
│   ├── AppDatabase.kt           ← base de datos Room (singleton)
│   ├── TransaccionRepository.kt ← abstrae Room + API
│   ├── TransaccionAdapter.kt    ← RecyclerView adapter
│   ├── InicioFragment.kt        ← pestaña Inicio
│   ├── TransaccionesFragment.kt ← pestaña Movimientos
│   ├── EstadisticasFragment.kt  ← pestaña Estadísticas
│   ├── Extensions.kt            ← formatearCOP(), mostrar(), ocultar()
│   ├── api/
│   │   ├── ApiService.kt        ← endpoint @GET "latest"
│   │   ├── RetrofitClient.kt    ← singleton Retrofit (BASE_URL)
│   │   ├── TasaCambioResponse.kt← data class del JSON de la API
│   │   └── ResultadoApi.kt      ← sealed class Cargando/Exito/Error
│   └── modelo/
│       ├── Transaccion.kt       ← @Entity data class (tabla SQLite)
│       └── Categoria.kt         ← enum con emoji + color
└── res/
    ├── layout/
    │   ├── activity_main.xml        ← fragmentContainer + BottomNav
    │   ├── activity_agregar.xml     ← formulario
    │   ├── fragment_inicio.xml      ← balance card + stats + últimas 5
    │   ├── fragment_transacciones.xml← lista + botón agregar
    │   ├── fragment_estadisticas.xml ← stats + tasas API
    │   └── item_transaccion.xml     ← cada fila de la lista
    ├── menu/
    │   └── bottom_menu.xml          ← 3 items del BottomNav
    ├── drawable/
    │   ├── bg_balance_gradient.xml  ← gradiente verde de la tarjeta
    │   ├── bg_icon_circle.xml       ← círculo del emoji de categoría
    │   ├── bg_monto_ingreso.xml     ← fondo verde del badge de monto
    │   ├── bg_monto_gasto.xml       ← fondo rojo del badge de monto
    │   └── ic_add.xml               ← ícono "+" del FAB
    └── values/
        ├── colors.xml               ← verde_primario, rojo_gasto, etc.
        ├── strings.xml              ← app_name
        └── themes.xml               ← MaterialComponents.Light.NoActionBar
```
