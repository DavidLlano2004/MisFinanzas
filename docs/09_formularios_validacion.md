# Capítulo 9: Formularios y validación

## Objetivo

Crear el formulario de `AgregarActivity` con validación en tiempo real, estados de carga del botón y buenas prácticas de UX con Material Design.

---

## 9.1 TextInputLayout: campo con estado

`TextInputLayout` envuelve a un `EditText` y añade etiqueta flotante, mensajes de error y contadores:

```xml
<com.google.android.material.textfield.TextInputLayout
    android:id="@+id/tilMonto"
    style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox"
    android:hint="Monto"
    app:prefixText="$ ">

    <com.google.android.material.textfield.TextInputEditText
        android:id="@+id/etMonto"
        android:inputType="numberDecimal" />
</com.google.android.material.textfield.TextInputLayout>
```

Mostrar error desde Kotlin:

```kotlin
binding.tilMonto.error = "El monto debe ser mayor a 0"
binding.tilMonto.error = null  // limpiar el error
```

---

## 9.2 Validación en tiempo real con doAfterTextChanged

```kotlin
// core-ktx incluye esta extensión
binding.etMonto.doAfterTextChanged { validarFormulario() }
binding.etDescripcion.doAfterTextChanged { validarFormulario() }

private fun validarFormulario() {
    val montoTxt = binding.etMonto.text?.toString() ?: ""
    val montoValido = montoTxt.isNotBlank()
        && montoTxt.toDoubleOrNull() != null
        && montoTxt.toDouble() > 0

    binding.tilMonto.error = when {
        montoTxt.isBlank()               -> null               // no mostrar error si vacío
        montoTxt.toDoubleOrNull() == null -> "Número inválido"
        montoTxt.toDouble() <= 0          -> "Debe ser > 0"
        else                              -> null
    }

    binding.btnGuardar.isEnabled = montoValido && descripcionValida
    binding.btnGuardar.alpha     = if (montoValido && descripcionValida) 1f else 0.5f
}
```

---

## 9.3 Spinner con ArrayAdapter

```kotlin
val categorias = Categoria.values().filter { !it.esIngreso() }
val adapter = ArrayAdapter(
    this,
    android.R.layout.simple_spinner_item,
    categorias.map { "${it.emoji} ${it.etiqueta}" }
)
adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
binding.spCategoria.adapter = adapter
binding.spCategoria.tag = categorias  // guardar la lista para recuperarla al guardar
```

Leer la categoría seleccionada:

```kotlin
@Suppress("UNCHECKED_CAST")
val cats = binding.spCategoria.tag as List<Categoria>
val categoria = cats[binding.spCategoria.selectedItemPosition]
```

---

## 9.4 Swipe para eliminar con Snackbar de deshacer

```kotlin
val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    override fun onMove(...) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val lista = viewModel.transacciones.value ?: return
        val transaccion = lista[viewHolder.adapterPosition]
        viewModel.eliminarTransaccion(transaccion)

        Snackbar.make(binding.root, "Eliminada", Snackbar.LENGTH_LONG)
            .setAction("Deshacer") { viewModel.agregarTransaccion(transaccion) }
            .show()
    }
}
ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.rvTransacciones)
```

---

## Resumen

| Concepto | Para qué |
|----------|----------|
| `TextInputLayout` | Campo con etiqueta, error y contador |
| `doAfterTextChanged` | Validar en tiempo real mientras el usuario escribe |
| `button.isEnabled` | Habilitar/deshabilitar el botón según validación |
| `Spinner` + `ArrayAdapter` | Lista desplegable de opciones |
| `ItemTouchHelper` | Detectar swipe en RecyclerView |
| `Snackbar` con `setAction` | Mensaje temporal con opción de deshacer |

**Anterior:** [← Capítulo 8 — Room](08_persistencia_room.md) | **Siguiente:** [Capítulo 10 — Fragments →](10_fragments_navegacion.md)
