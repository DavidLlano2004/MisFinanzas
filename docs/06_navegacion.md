# Capítulo 6: Navegación entre pantallas

## Objetivo

Agregar una segunda pantalla (`AgregarActivity`) para crear nuevas transacciones. Aprender a navegar con `Intent` y pasar datos de regreso con `registerForActivityResult`.

---

## 6.1 Intent: la forma de navegar en Android

```kotlin
// Abrir una Activity
val intent = Intent(this, AgregarActivity::class.java)
startActivity(intent)

// Abrir y esperar resultado (patrón moderno)
val lanzar = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { resultado ->
    if (resultado.resultCode == Activity.RESULT_OK) {
        val dato = resultado.data?.getStringExtra("CLAVE")
    }
}
lanzar.launch(intent)
```

---

## 6.2 Registrar la Activity en el Manifest

Toda Activity debe declararse en `AndroidManifest.xml`:

```xml
<activity
    android:name=".AgregarActivity"
    android:exported="false" />
```

---

## 6.3 Devolver resultado desde AgregarActivity

```kotlin
// En AgregarActivity, al terminar:
setResult(Activity.RESULT_OK, Intent().apply {
    putExtra("NUEVA_TRANSACCION", transaccion)  // Serializable
})
finish()

// Si el usuario cancela:
finish()  // setResult no es necesario (RESULT_CANCELED por defecto)
```

---

## 6.4 Pasar objetos con Serializable

Para pasar objetos entre Activities, la clase debe implementar `Serializable`:

```kotlin
data class Transaccion(...) : Serializable
```

Recuperar en la Activity receptora:

```kotlin
val obj = intent.getSerializableExtra("CLAVE") as? MiClase
```

---

## Resumen

| Concepto | Para qué |
|----------|----------|
| `Intent` | Navegar entre Activities |
| `registerForActivityResult` | Abrir Activity y recibir resultado |
| `setResult()` + `finish()` | Devolver datos al llamador |
| `Serializable` | Pasar objetos entre Activities |
| `AndroidManifest.xml` | Registrar toda Activity de la app |

**Anterior:** [← Capítulo 5 — RecyclerView](05_listas_recyclerview.md) | **Siguiente:** [Capítulo 7 — ViewModel →](07_ciclo_vida_viewmodel.md)
