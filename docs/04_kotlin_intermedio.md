# Capítulo 4: Kotlin intermedio — Clases, data classes y enums

## Objetivo

Aprender programación orientada a objetos en Kotlin para crear el modelo de datos de MisFinanzas. Al final de este capítulo tendremos `Categoria` y `Transaccion` como clases propias.

---

## 4.1 Clases en Kotlin

```kotlin
// Clase básica: las propiedades van en el constructor primario
class Cuenta(
    val nombre: String,   // val = solo lectura
    var balance: Double   // var = modificable
) {
    fun depositar(monto: Double) {
        balance += monto
    }

    fun resumen(): String = "$nombre: $$balance"
}

val cuenta = Cuenta("Ahorros", 1000000.0)  // sin "new"
cuenta.depositar(500000.0)
println(cuenta.resumen())  // "Ahorros: $1500000.0"
```

### Valores por defecto

```kotlin
class Transaccion(
    val monto: Double,
    val descripcion: String,
    val categoria: String = "General",          // opcional
    val fecha: Long = System.currentTimeMillis() // opcional
)

val t1 = Transaccion(50000.0, "Almuerzo", "Comida")  // con categoría
val t2 = Transaccion(2500000.0, "Salario")            // usa defaults
```

---

## 4.2 Data Classes

Las `data class` generan automáticamente `toString()`, `equals()`, `hashCode()` y `copy()`:

```kotlin
data class Transaccion(
    val id: Int,
    val monto: Double,
    val descripcion: String,
    val categoria: String
)

val t1 = Transaccion(1, -15000.0, "Almuerzo", "Comida")
val t2 = Transaccion(1, -15000.0, "Almuerzo", "Comida")

println(t1)        // Transaccion(id=1, monto=-15000.0, ...)
println(t1 == t2)  // true — compara por contenido, no por referencia

// copy() crea una copia cambiando solo los campos indicados
val t3 = t1.copy(monto = -20000.0)
```

---

## 4.3 Enums

Los enums representan un conjunto fijo de opciones. Cada valor puede tener propiedades:

```kotlin
enum class Categoria(
    val emoji: String,
    val etiqueta: String,
    val colorFondoHex: String
) {
    SALARIO("💰", "Salario", "#E8F5E9"),
    FREELANCE("💻", "Freelance", "#E0F2F1"),
    COMIDA("🛒", "Comida", "#FFF3E0"),
    TRANSPORTE("🚇", "Transporte", "#E3F2FD"),
    SERVICIOS("💡", "Servicios", "#F3E5F5"),
    ENTRETENIMIENTO("🎬", "Entretenimiento", "#FCE4EC"),
    SALUD("🏥", "Salud", "#FFEBEE"),
    EDUCACION("📚", "Educación", "#E8EAF6"),
    OTROS("📦", "Otros", "#F5F5F5");

    fun esIngreso(): Boolean = this == SALARIO || this == FREELANCE
}

// Uso
val cat = Categoria.COMIDA
println(cat.emoji)         // "🛒"
println(cat.esIngreso())   // false

// when con enum: el compilador verifica que cubras todos los casos
val color = when (cat) {
    Categoria.SALARIO, Categoria.FREELANCE -> "#2E7D32"
    Categoria.COMIDA -> "#E65100"
    else -> "#546E7A"
}
```

---

## 4.4 Herencia

En Kotlin las clases son `final` por defecto. Hay que marcarlas `open` para permitir herencia:

```kotlin
open class Movimiento(val monto: Double, val descripcion: String) {
    open fun resumen(): String = "$descripcion: $$monto"
}

class Ingreso(monto: Double, descripcion: String, val fuente: String)
    : Movimiento(monto, descripcion) {
    override fun resumen(): String = "✅ $descripcion ($fuente): +$$monto"
}

class Gasto(monto: Double, descripcion: String, val categoria: String)
    : Movimiento(monto, descripcion) {
    override fun resumen(): String = "❌ $descripcion ($categoria): -$$monto"
}

// Polimorfismo: lista mixta, cada objeto usa SU versión de resumen()
val movimientos: List<Movimiento> = listOf(
    Ingreso(2500000.0, "Salario", "Empresa"),
    Gasto(150000.0, "Almuerzo", "Comida")
)
movimientos.forEach { println(it.resumen()) }
```

---

## 4.5 Companion object

El `companion object` contiene funciones y constantes que pertenecen a la clase, no a instancias:

```kotlin
data class Transaccion(
    val id: Int,
    val monto: Double,
    val descripcion: String,
    val categoria: Categoria,
    val fecha: String = "Hoy"
) : Serializable {

    val esIngreso: Boolean get() = monto > 0

    fun montoFormateado(): String {
        val signo = if (esIngreso) "+" else "-"
        return "$signo $ ${String.format(Locale.US, "%,.0f", Math.abs(monto))}"
    }

    companion object {
        // Se accede como Transaccion.datosDePrueba() sin crear una instancia
        fun datosDePrueba(): List<Transaccion> = listOf(
            Transaccion(1, 2500000.0, "Salario mensual", Categoria.SALARIO),
            Transaccion(2, -150000.0, "Mercado",         Categoria.COMIDA),
            // ...
        )
    }
}
```

---

## 4.6 Archivos creados en este capítulo

| Archivo | Qué hace |
|---------|----------|
| `modelo/Categoria.kt` | Enum con las categorías, emoji, color de fondo |
| `modelo/Transaccion.kt` | Data class con `esIngreso`, `montoFormateado()`, `datosDePrueba()` |

---

## Resumen

| Concepto | Para qué |
|----------|----------|
| `class` | Agrupar datos y comportamiento |
| `data class` | Modelo de datos con toString/equals/copy automáticos |
| `enum class` | Conjunto fijo de opciones con propiedades |
| `open` / `override` | Herencia y polimorfismo |
| `companion object` | Constantes y funciones "estáticas" |
| `Serializable` | Pasar objetos entre pantallas con Intent |

---

**Anterior:** [← Capítulo 3 — Layouts y vistas](03_layouts_vistas.md) | **Siguiente:** [Capítulo 5 — RecyclerView →](05_listas_recyclerview.md)
