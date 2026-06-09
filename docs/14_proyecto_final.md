# Capítulo 14: Proyecto final — MisFinanzas completa

## Lo que construimos

A lo largo de 14 capítulos, construimos MisFinanzas desde cero:

| Funcionalidad | Cap. | Tecnología |
|---------------|------|------------|
| Proyecto base y Hola Mundo | 01 | Activity, View Binding |
| Cálculo de balance | 02 | Kotlin básico, funciones |
| Interfaz con tarjetas | 03 | LinearLayout, CardView |
| Modelo de datos | 04 | Data classes, enums |
| Lista de transacciones | 05 | RecyclerView, Adapter |
| Agregar transacciones | 06 | Intents, navegación |
| Estado que sobrevive rotaciones | 07 | ViewModel, LiveData |
| Datos persistentes | 08 | Room (SQLite) |
| Validación de formularios | 09 | TextInputLayout, Material Design |
| Navegación por pestañas | 10 | Fragments, BottomNavigationView |
| Consulta de tasas de cambio | 11 | Retrofit, APIs REST |
| Arquitectura limpia | 12 | MVVM, Repository pattern |
| Kotlin avanzado | 13 | Coroutines, sealed classes, lambdas |

---

## Dependencias completas (`app/build.gradle.kts`)

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.fragment:fragment-ktx:1.7.1")

    // Room (KSP)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
```

---

## Convenciones de nombres

| Elemento | Convención | Ejemplo |
|----------|-----------|---------|
| Clases | PascalCase | `MainActivity`, `TransaccionAdapter` |
| Funciones y variables | camelCase | `obtenerTodas()`, `listaTransacciones` |
| Constantes | UPPER_SNAKE_CASE | `BASE_URL`, `MAX_DESC` |
| Layouts XML | snake_case | `activity_main.xml`, `item_transaccion.xml` |
| IDs en XML | camelCase con prefijo | `tvTitulo`, `etMonto`, `btnGuardar` |

### Prefijos para IDs XML

`tv` TextView · `et` EditText · `btn` Button · `rv` RecyclerView · `til` TextInputLayout · `sp` Spinner · `pb` ProgressBar · `fab` FloatingActionButton

---

## Buenas prácticas

```kotlin
// ✅ val siempre que sea posible
val nombre = "Juan"

// ✅ apply para configurar objetos
val intent = Intent(this, DetalleActivity::class.java).apply {
    putExtra("ID", 123)
}

// ✅ Elvis operator en lugar de !!
val nombre = intent.getStringExtra("NOMBRE") ?: "Sin nombre"

// ✅ viewLifecycleOwner en Fragments
viewModel.datos.observe(viewLifecycleOwner) { ... }

// ✅ Limpiar binding en onDestroyView
override fun onDestroyView() { super.onDestroyView(); _binding = null }

// ✅ Dispatchers.IO para red y disco
val datos = withContext(Dispatchers.IO) { apiService.obtenerDatos() }

// ❌ GlobalScope — no se cancela, causa memory leaks
GlobalScope.launch { ... }
```

---

## Equivalencias Flutter ↔ Android Nativo

| Flutter (Dart) | Android (Kotlin) |
|----------------|-----------------|
| Widget | View |
| StatefulWidget + setState | Activity/Fragment + ViewModel + LiveData |
| ListView.builder | RecyclerView + Adapter |
| Navigator.push | startActivity(Intent) |
| Navigator.pop | finish() |
| Provider / ChangeNotifier | ViewModel + LiveData |
| sqflite | Room |
| http / dio | Retrofit |
| async / await | suspend / launch |
| pubspec.yaml | build.gradle.kts |

---

## Problemas comunes

| Problema | Solución |
|----------|----------|
| "Unresolved reference" | Alt+Enter para importar la clase |
| "Cannot access database on the main thread" | Usar `suspend` + `viewModelScope.launch` |
| La lista no se actualiza | Verificar que hay un observer activo |
| Crash al rotar | Usar ViewModel en lugar de variables en Activity |
| Binding null en Fragment | Solo usar entre onCreateView y onDestroyView |
| "Activity not found" | Registrar la Activity en AndroidManifest.xml |
| Retrofit falla silenciosamente | Agregar `<uses-permission INTERNET />` |
| Room no compila | Verificar que el plugin KSP está agregado |

---

## ¿Qué sigue?

| Tema | Para qué |
|------|----------|
| Jetpack Compose | UI declarativa (el futuro de Android) |
| Navigation Component | Navegación más robusta entre Fragments |
| Hilt / Dagger | Inyección de dependencias automática |
| DataStore | Reemplazo moderno de SharedPreferences |
| Testing | Pruebas unitarias y de UI |
| Firebase | Backend as a Service |

---

**Anterior:** [← Capítulo 13 — Kotlin avanzado](13_kotlin_avanzado.md) | **Inicio:** [README →](../README.md)
