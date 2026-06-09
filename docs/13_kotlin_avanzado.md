# Capítulo 13: Kotlin avanzado

## Objetivo

Profundizar en características de Kotlin que hacen el código más expresivo: coroutines, sealed classes, scope functions, lambdas y operaciones funcionales sobre colecciones.

---

## 13.1 Coroutines: dispatchers y withContext

```kotlin
viewModelScope.launch {
    // Estamos en Main (hilo principal)
    binding.progressBar.mostrar()

    val datos = withContext(Dispatchers.IO) {
        // Cambia temporalmente a IO (hilo de red/disco)
        apiService.obtenerDatos()
    }

    // Volvemos a Main automáticamente
    binding.progressBar.ocultar()
    adapter.submitList(datos)
}
```

| Dispatcher | Usar para |
|------------|-----------|
| `Main` | Actualizar UI |
| `IO` | Red, base de datos, archivos |
| `Default` | Cálculos pesados en CPU |

---

## 13.2 Sealed class a fondo

```kotlin
sealed class EstadoPantalla {
    object Cargando : EstadoPantalla()
    data class Contenido(val transacciones: List<Transaccion>, val balance: Double) : EstadoPantalla()
    object Vacio : EstadoPantalla()
    data class Error(val mensaje: String) : EstadoPantalla()
}

// El compilador garantiza que se cubran TODOS los casos
when (estado) {
    is EstadoPantalla.Cargando  -> { ... }
    is EstadoPantalla.Contenido -> { adapter.submitList(estado.transacciones) }  // smart cast
    is EstadoPantalla.Vacio     -> { ... }
    is EstadoPantalla.Error     -> { binding.tvError.text = estado.mensaje }
}
```

---

## 13.3 Scope functions

```kotlin
// let: ejecutar si no es null, usar "it"
intent.getStringExtra("NOMBRE")?.let { nombre ->
    binding.tvNombre.text = nombre
}

// apply: configurar objeto, usa "this", retorna el objeto
val intent = Intent(this, DetalleActivity::class.java).apply {
    putExtra("ID", 123)
    putExtra("NOMBRE", "Producto")
}

// also: acción secundaria (log), usa "it", retorna el objeto
val transacciones = dao.obtenerLista().also { lista ->
    Log.d("TAG", "Cargadas ${lista.size} transacciones")
}

// with: múltiples operaciones sobre un objeto
with(binding) {
    tvBalance.text  = balance.formatearCOP()
    tvIngresos.text = ingresos.formatearCOP()
    progressBar.ocultar()
}
```

---

## 13.4 Operaciones funcionales sobre colecciones

```kotlin
val transacciones = viewModel.transacciones.value ?: emptyList()

// filter: solo los que cumplen la condición
val gastos = transacciones.filter { !it.esIngreso }

// map: transformar cada elemento
val descripciones = transacciones.map { it.descripcion }

// groupBy: agrupar por clave → Map<Categoria, List<Transaccion>>
val porCategoria = gastos.groupBy { it.categoria }

// sumOf: suma con transformación
val totalGastos = gastos.sumOf { Math.abs(it.monto) }

// Encadenar operaciones — top 3 categorías de gasto:
val top3 = transacciones
    .filter { !it.esIngreso }
    .groupBy { it.categoria }
    .mapValues { (_, lista) -> lista.sumOf { Math.abs(it.monto) } }
    .toList()
    .sortedByDescending { it.second }
    .take(3)

// takeIf: retorna el objeto si cumple la condición, o null
transacciones
    .filter { !it.esIngreso }
    .takeIf { it.isNotEmpty() }
    ?.let { gastos ->
        val promedio = gastos.sumOf { Math.abs(it.monto) } / gastos.size
        binding.tvPromedio.text = promedio.formatearCOP()
    }
```

---

## 13.5 buildString

```kotlin
// Construir strings complejos de forma eficiente
val resumen = buildString {
    appendLine("📊 Top gastos:")
    top3.forEachIndexed { idx, (cat, total) ->
        appendLine("${idx + 1}. ${cat.emoji} ${cat.etiqueta}: ${total.formatearCOP()}")
    }
    append("Total: ${gastos.sumOf { Math.abs(it.monto) }.formatearCOP()}")
}
binding.tvResumen.text = resumen
```

---

## Resumen

| Concepto | Para qué |
|----------|----------|
| `withContext` | Cambiar de hilo dentro de una coroutine |
| `Dispatchers.IO` / `.Main` | Hilo de red vs hilo de UI |
| `sealed class` | Estados cerrados con datos diferentes |
| `let` | Ejecutar si no es null |
| `apply` | Configurar un objeto |
| `with` | Múltiples operaciones sobre un objeto |
| `filter`, `map`, `groupBy` | Operaciones funcionales sobre listas |
| `takeIf` | Retornar null si no cumple la condición |
| `buildString` | Construir strings complejos eficientemente |

**Anterior:** [← Capítulo 12 — MVVM](12_arquitectura_mvvm.md) | **Siguiente:** [Capítulo 14 — Proyecto final →](14_proyecto_final.md)
