# Capítulo 7: Ciclo de vida y ViewModel

## Objetivo

Entender por qué los datos se pierden al rotar el dispositivo y cómo el `ViewModel` + `LiveData` resuelven ese problema de forma elegante.

---

## 7.1 El problema: datos que se pierden

Al rotar el dispositivo, Android **destruye y recrea** la Activity. Cualquier variable que declares en la Activity se pierde.

```kotlin
// MAL: se reinicia al rotar
class MainActivity : AppCompatActivity() {
    private var contador = 0  // ← se vuelve 0 al rotar
}
```

---

## 7.2 ViewModel: sobrevive a rotaciones

El `ViewModel` vive más que la Activity. Android lo mantiene vivo durante rotaciones y lo destruye solo cuando el usuario sale definitivamente de la pantalla.

```kotlin
class MainViewModel : ViewModel() {
    private val _contador = MutableLiveData(0)
    val contador: LiveData<Int> = _contador

    fun incrementar() {
        _contador.value = (_contador.value ?: 0) + 1
    }
}

// En la Activity:
private val viewModel: MainViewModel by viewModels()  // requiere activity-ktx

override fun onCreate(...) {
    viewModel.contador.observe(this) { valor ->
        binding.tvContador.text = "$valor"
    }
}
```

---

## 7.3 LiveData: datos observables

`LiveData` es un contenedor de datos que notifica automáticamente a los observadores cuando cambia. Solo notifica cuando el ciclo de vida del observador está activo (en pantalla).

```
MutableLiveData ← el ViewModel escribe
LiveData        ← la Activity/Fragment solo lee (observa)
```

---

## 7.4 AndroidViewModel: con acceso al contexto

`AndroidViewModel` es como `ViewModel` pero recibe un `Application`. Lo usamos cuando necesitamos acceder a la base de datos (Room):

```kotlin
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.obtenerInstancia(application).transaccionDao()
}
```

---

## Dependencias necesarias

```kotlin
// app/build.gradle.kts
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
implementation("androidx.activity:activity-ktx:1.9.0")
```

---

## Resumen

| Concepto | Para qué |
|----------|----------|
| `ViewModel` | Sobrevive a rotaciones, separa lógica de la UI |
| `LiveData` | Datos observables que notifican cambios automáticamente |
| `MutableLiveData` | Versión modificable (solo dentro del ViewModel) |
| `by viewModels()` | Obtener o crear el ViewModel para esta Activity |
| `observe(this)` | Suscribirse a cambios del LiveData |

**Anterior:** [← Capítulo 6 — Navegación](06_navegacion.md) | **Siguiente:** [Capítulo 8 — Room →](08_persistencia_room.md)
