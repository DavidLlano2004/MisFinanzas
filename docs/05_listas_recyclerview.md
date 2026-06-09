# Capítulo 5: Listas con RecyclerView

## Objetivo

Mostrar la lista de transacciones usando RecyclerView — el componente estándar de Android para listas eficientes. Aprenderemos el patrón Adapter + ViewHolder.

---

## 5.1 ¿Qué es RecyclerView?

RecyclerView muestra listas largas de forma eficiente **reciclando** las vistas que salen de pantalla para reutilizarlas en los nuevos elementos que entran. Es el equivalente a `ListView.builder()` de Flutter.

Para usarlo necesitamos tres piezas:
1. **Layout del item** (`item_transaccion.xml`) — cómo se ve cada elemento
2. **Adapter + ViewHolder** (`TransaccionAdapter.kt`) — conecta los datos con las vistas
3. **RecyclerView en el layout** (`activity_main.xml`) — el contenedor en pantalla

---

## 5.2 Layout del item (`item_transaccion.xml`)

```xml
<!-- Se repite una vez por cada transacción -->
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginHorizontal="16dp"
    android:layout_marginVertical="5dp"
    app:cardCornerRadius="14dp"
    app:cardElevation="2dp">

    <LinearLayout android:orientation="horizontal" android:padding="14dp">

        <!-- Círculo con emoji de la categoría -->
        <FrameLayout android:id="@+id/flIconBg" android:layout_width="48dp"
            android:layout_height="48dp" android:background="@drawable/bg_icon_circle">
            <TextView android:id="@+id/tvEmoji" android:gravity="center" android:textSize="22sp"
                android:layout_width="match_parent" android:layout_height="match_parent"/>
        </FrameLayout>

        <!-- Descripción + categoría + fecha -->
        <LinearLayout android:layout_width="0dp" android:layout_weight="1" ...>
            <TextView android:id="@+id/tvDescripcion" android:textStyle="bold"/>
            <LinearLayout android:orientation="horizontal">
                <TextView android:id="@+id/tvCategoria"/>
                <TextView android:text=" · "/>
                <TextView android:id="@+id/tvFecha"/>
            </LinearLayout>
        </LinearLayout>

        <!-- Badge del monto (fondo verde o rojo) -->
        <TextView android:id="@+id/tvMonto" android:textStyle="bold"/>
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>
```

---

## 5.3 El Adapter (`TransaccionAdapter.kt`)

El Adapter tiene tres responsabilidades:

```kotlin
class TransaccionAdapter(
    private val transacciones: List<Transaccion>,
    private val onItemClick: (Transaccion) -> Unit  // callback al hacer click
) : RecyclerView.Adapter<TransaccionAdapter.ViewHolder>() {

    // ViewHolder guarda referencias a las vistas de UN item (evita buscarlas cada vez)
    inner class ViewHolder(val binding: ItemTransaccionBinding) :
        RecyclerView.ViewHolder(binding.root)

    // 1. CREAR: inflar el XML del item (ocurre pocas veces)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransaccionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    // 2. VINCULAR: poner los datos en la vista (ocurre para cada item visible)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val t = transacciones[position]

        holder.binding.tvEmoji.text = t.categoria.emoji
        holder.binding.tvDescripcion.text = t.descripcion
        holder.binding.tvCategoria.text = t.categoria.etiqueta
        holder.binding.tvFecha.text = t.fecha
        holder.binding.tvMonto.text = t.montoFormateado()

        // Verde para ingresos, rojo para gastos
        if (t.esIngreso) {
            holder.binding.tvMonto.setTextColor(ctx.getColor(R.color.verde_primario))
            holder.binding.tvMonto.setBackgroundResource(R.drawable.bg_monto_ingreso)
        } else {
            holder.binding.tvMonto.setTextColor(ctx.getColor(R.color.rojo_gasto))
            holder.binding.tvMonto.setBackgroundResource(R.drawable.bg_monto_gasto)
        }

        // Color del círculo según la categoría (mutate() evita compartir el drawable)
        val bg = holder.binding.flIconBg.background.mutate() as? GradientDrawable
        bg?.setColor(Color.parseColor(t.categoria.colorFondoHex))

        holder.itemView.setOnClickListener { onItemClick(t) }
    }

    // 3. CONTAR: cuántos items hay en total
    override fun getItemCount(): Int = transacciones.size
}
```

---

## 5.4 Conectar en la Activity

```kotlin
private fun configurarRecyclerView() {
    adapter = TransaccionAdapter(transacciones) { t ->
        // Se ejecuta cuando el usuario toca un item
        Toast.makeText(this, "${t.descripcion}: ${t.montoFormateado()}", Toast.LENGTH_SHORT).show()
    }

    binding.rvTransacciones.layoutManager = LinearLayoutManager(this)
    binding.rvTransacciones.adapter = adapter
}
```

El **RecyclerView en el XML** usa `layout_weight="1"` para ocupar todo el espacio restante debajo del header:

```xml
<androidx.recyclerview.widget.RecyclerView
    android:id="@+id/rvTransacciones"
    android:layout_width="match_parent"
    android:layout_height="0dp"
    android:layout_weight="1"
    android:clipToPadding="false"
    android:paddingBottom="88dp"/>
```

---

## 5.5 ¿Por qué `mutate()` en el drawable?

Los drawables cargados desde recursos son **compartidos** entre todas las vistas que los usan. Si llamamos `setColor()` sin `mutate()`, cambiamos el color de TODOS los íconos a la vez. `mutate()` crea una copia independiente:

```kotlin
// MAL: cambia el color en todos los items
(flIconBg.background as GradientDrawable).setColor(color)

// BIEN: crea una copia independiente para este item
val bg = flIconBg.background.mutate() as? GradientDrawable
bg?.setColor(color)
```

---

## 5.6 GridLayoutManager (extra)

Para mostrar en cuadrícula en lugar de lista:

```kotlin
// 2 columnas
binding.rvTransacciones.layoutManager = GridLayoutManager(this, 2)
```

---

## Resumen

| Concepto | Para qué |
|----------|----------|
| `RecyclerView` | Lista eficiente que recicla vistas |
| `Adapter` | Puente entre datos y vistas |
| `ViewHolder` | Contenedor de referencias a vistas de un item |
| `onCreateViewHolder` | Infla el XML del item (pocas veces) |
| `onBindViewHolder` | Pone datos en la vista (para cada item visible) |
| `getItemCount` | Le dice al RV cuántos items hay |
| `LinearLayoutManager` | Lista vertical (o horizontal) |
| `GridLayoutManager` | Cuadrícula de N columnas |
| `mutate()` | Hace un drawable independiente para no compartir estado |

---

**Anterior:** [← Capítulo 4 — Kotlin intermedio](04_kotlin_intermedio.md) | **Siguiente:** [Capítulo 6 — Navegación entre pantallas →](06_navegacion.md)
