# Capítulo 12: Arquitectura MVVM

## Objetivo

Formalizar la arquitectura MVVM (Model-View-ViewModel) con el patrón Repository. Separa responsabilidades para que el código sea mantenible, testeable y escalable.

---

## 12.1 Las tres capas de MVVM

```
┌─────────────┐  observa   ┌─────────────┐  usa    ┌─────────────────────┐
│    VIEW     │ ◄────────  │  VIEWMODEL  │ ──────► │       MODEL         │
│ Activities  │            │  LiveData   │ ◄─────  │  Repository         │
│ Fragments   │ ──────────►│  Coroutines │  datos  │  Room DAO  Retrofit │
└─────────────┘  eventos   └─────────────┘         └─────────────────────┘
```

### Regla de oro
- La **View** solo habla con el **ViewModel**
- El **ViewModel** solo habla con el **Repository**
- El **Repository** habla con Room y Retrofit

---

## 12.2 El Repository

El Repository es el puente entre datos y el ViewModel. Abstrae el origen:

```kotlin
class TransaccionRepository(private val dao: TransaccionDao) {

    // Room
    val todasLasTransacciones = dao.obtenerTodas()
    val balance = dao.obtenerBalance()

    suspend fun insertar(t: Transaccion) = dao.insertar(t)
    suspend fun eliminar(t: Transaccion) = dao.eliminar(t)

    // Retrofit
    suspend fun obtenerTasasCambio(base: String = "USD") =
        RetrofitClient.apiService.obtenerTasas(base)
}
```

**¿Por qué?** El ViewModel no sabe si los datos vienen de Room, de Retrofit o de caché. Si cambias la fuente de datos, solo tocas el Repository.

---

## 12.3 ViewModel con Repository

```kotlin
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransaccionRepository

    init {
        val dao = AppDatabase.obtenerInstancia(application).transaccionDao()
        repository = TransaccionRepository(dao)
    }

    val transacciones = repository.todasLasTransacciones
    val balance = repository.balance

    fun agregarTransaccion(t: Transaccion) {
        viewModelScope.launch { repository.insertar(t) }
    }
}
```

---

## 12.4 Extension functions

```kotlin
// Extensions.kt — funciones de extensión reutilizables
fun Double.formatearCOP(): String = "$ ${String.format("%,.0f", this)}"
fun View.mostrar()  { visibility = View.VISIBLE }
fun View.ocultar()  { visibility = View.GONE }

// Uso en cualquier Fragment/Activity del mismo paquete:
binding.tvBalance.text = balance.formatearCOP()
binding.progressBar.ocultar()
```

---

## 12.5 Estructura de archivos final

```
java/com/ejemplo/misfinanzas/
├── MainActivity.kt
├── MainViewModel.kt
├── AgregarActivity.kt
├── TransaccionDao.kt
├── AppDatabase.kt
├── TransaccionRepository.kt
├── TransaccionAdapter.kt
├── InicioFragment.kt
├── TransaccionesFragment.kt
├── EstadisticasFragment.kt
├── Extensions.kt
├── api/
│   ├── ApiService.kt
│   ├── RetrofitClient.kt
│   ├── TasaCambioResponse.kt
│   └── ResultadoApi.kt
└── modelo/
    ├── Transaccion.kt
    └── Categoria.kt
```

---

## Resumen

| Concepto | Para qué |
|----------|----------|
| MVVM | View-ViewModel-Model: separación de responsabilidades |
| Repository | Abstrae el origen de datos |
| `AndroidViewModel` | ViewModel con acceso al contexto de la app |
| Extension functions | Añadir funciones a clases existentes sin modificarlas |
| Inyección manual de dependencias | Pasar el DAO al Repository por constructor |

**Anterior:** [← Capítulo 11 — Retrofit](11_retrofit_api.md) | **Siguiente:** [Capítulo 13 — Kotlin avanzado →](13_kotlin_avanzado.md)
