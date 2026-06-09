# Capítulo 11: Consumo de APIs con Retrofit

## Objetivo

Consultar tasas de cambio desde una API externa usando Retrofit. Aprender a hacer peticiones HTTP, parsear JSON y manejar estados de carga/error.

---

## 11.1 ¿Qué es Retrofit?

Retrofit convierte automáticamente respuestas JSON en objetos Kotlin. Es el estándar para consumir APIs REST en Android.

| En Flutter (http/dio) | En Android (Retrofit) |
|----------------------|----------------------|
| Funciones sueltas | Interfaz con anotaciones |
| `jsonDecode` + `fromJson` | Gson + data class |
| `async` / `await` | `suspend` / coroutines |
| `http.Client` | `Retrofit.Builder()` |

---

## 11.2 Data class para la respuesta JSON

```json
{ "base": "USD", "rates": { "COP": 4150.50, "EUR": 0.92 } }
```

```kotlin
data class TasaCambioResponse(
    val base: String,
    val rates: Map<String, Double>
)
```

---

## 11.3 Interfaz de endpoints

```kotlin
interface ApiService {
    @GET("latest")
    suspend fun obtenerTasas(@Query("base") monedaBase: String = "USD"): TasaCambioResponse
}
```

---

## 11.4 Singleton del cliente

```kotlin
object RetrofitClient {
    private const val BASE_URL = "https://api.exchangerate-api.com/v4/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
```

---

## 11.5 Sealed class para estados

```kotlin
sealed class ResultadoApi<out T> {
    object Cargando : ResultadoApi<Nothing>()
    data class Exito<T>(val datos: T) : ResultadoApi<T>()
    data class Error(val mensaje: String) : ResultadoApi<Nothing>()
}
```

**¿Por qué sealed class?** Porque cada estado tiene datos diferentes. El compilador verifica que todos los casos estén cubiertos en un `when`.

---

## 11.6 Llamada en el ViewModel

```kotlin
fun consultarTasas() {
    _tasasCambio.value = ResultadoApi.Cargando
    viewModelScope.launch {
        try {
            val respuesta = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.obtenerTasas()
            }
            _tasasCambio.value = ResultadoApi.Exito(respuesta)
        } catch (e: UnknownHostException) {
            _tasasCambio.value = ResultadoApi.Error("Sin conexión a internet")
        } catch (e: Exception) {
            _tasasCambio.value = ResultadoApi.Error("Error: ${e.message}")
        }
    }
}
```

---

## 11.7 Observar en el Fragment

```kotlin
viewModel.tasasCambio.observe(viewLifecycleOwner) { resultado ->
    when (resultado) {
        is ResultadoApi.Cargando -> { binding.progressTasas.mostrar() }
        is ResultadoApi.Exito    -> {
            binding.progressTasas.ocultar()
            binding.tvTasas.text = "1 USD = ${resultado.datos.rates["COP"]} COP"
        }
        is ResultadoApi.Error -> {
            binding.progressTasas.ocultar()
            binding.tvTasas.text = resultado.mensaje
        }
    }
}
```

---

## Dependencias

```kotlin
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.google.code.gson:gson:2.10.1")
```

Asegurarse de tener el permiso en el Manifest:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

---

## Resumen

| Concepto | Para qué |
|----------|----------|
| `@GET` / `@POST` | Definir el método HTTP del endpoint |
| `@Query` | Agregar parámetros a la URL |
| `GsonConverterFactory` | Convertir JSON a objetos Kotlin |
| `suspend` en ApiService | Ejecutar en coroutines |
| `Dispatchers.IO` | Hilo para operaciones de red |
| `withContext` | Cambiar de hilo dentro de una coroutine |
| `sealed class` | Representar estados Cargando/Exito/Error |

**Anterior:** [← Capítulo 10 — Fragments](10_fragments_navegacion.md) | **Siguiente:** [Capítulo 12 — MVVM →](12_arquitectura_mvvm.md)
