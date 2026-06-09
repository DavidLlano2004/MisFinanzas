# Capítulo 10: Fragments y Bottom Navigation

## Objetivo

Reorganizar MisFinanzas en tres pestañas usando `Fragment` y `BottomNavigationView`: Inicio (resumen), Movimientos (lista completa) y Estadísticas.

---

## 10.1 ¿Qué es un Fragment?

Un Fragment es una porción reutilizable de interfaz que vive dentro de una Activity. La Activity es el contenedor; los Fragments son el contenido que cambia.

| Aspecto | Activity | Fragment |
|---------|----------|----------|
| Ciclo de vida | Independiente | Depende de la Activity padre |
| Registro | En AndroidManifest.xml | No se registra |
| Binding | `lateinit var binding` | `_binding` nullable + `binding` non-null |
| Contexto | `this` | `requireContext()` |
| ViewModel compartido | `by viewModels()` | `by activityViewModels()` |

---

## 10.2 Patrón de binding seguro en Fragments

```kotlin
class MiFragment : Fragment() {

    // _binding puede ser null (entre onDestroyView y onDestroy)
    private var _binding: FragmentMiBinding? = null
    // binding lanza excepción si se usa fuera del ciclo de vida
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Configurar listeners y observers aquí
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Evitar memory leaks
    }
}
```

---

## 10.3 Compartir ViewModel entre Fragments

```kotlin
// En la Activity: crea el ViewModel
private val viewModel: MainViewModel by viewModels()

// En cada Fragment: accede al MISMO ViewModel de la Activity
private val viewModel: MainViewModel by activityViewModels()

// Ambos usan la misma instancia → datos sincronizados
```

---

## 10.4 BottomNavigationView

```xml
<!-- activity_main.xml -->
<LinearLayout android:orientation="vertical">

    <FrameLayout android:id="@+id/fragmentContainer"
        android:layout_weight="1" />

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottomNav"
        app:menu="@menu/bottom_menu"
        app:labelVisibilityMode="labeled" />
</LinearLayout>
```

```kotlin
// MainActivity.kt
binding.bottomNav.setOnItemSelectedListener { item ->
    when (item.itemId) {
        R.id.nav_inicio        -> cargarFragment(InicioFragment())
        R.id.nav_transacciones -> cargarFragment(TransaccionesFragment())
        R.id.nav_estadisticas  -> cargarFragment(EstadisticasFragment())
    }
    true  // indica que el item fue manejado
}

private fun cargarFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction()
        .replace(R.id.fragmentContainer, fragment)
        .commit()
}
```

---

## 10.5 viewLifecycleOwner vs this

```kotlin
// En un Fragment, usar viewLifecycleOwner (NO "this") para observar LiveData:
viewModel.datos.observe(viewLifecycleOwner) { ... }

// Por qué: el Fragment puede existir sin vista (en el back stack).
// viewLifecycleOwner representa el lifecycle de la VISTA, no del Fragment.
// Si usas "this", el observer puede actualizarse cuando la vista no existe → crash.
```

---

## Dependencia necesaria

```kotlin
implementation("androidx.fragment:fragment-ktx:1.7.1")  // activityViewModels()
```

---

## Resumen

| Concepto | Para qué |
|----------|----------|
| `Fragment` | Porción reutilizable de UI dentro de una Activity |
| `_binding` / `binding` | Patrón de binding seguro en Fragments |
| `onCreateView` | Inflar el layout del Fragment |
| `onDestroyView` | Limpiar el binding (evitar memory leaks) |
| `viewLifecycleOwner` | Lifecycle del Fragment para observar LiveData |
| `activityViewModels()` | Compartir ViewModel entre Activity y Fragments |
| `BottomNavigationView` | Barra de navegación inferior con pestañas |
| `.replace()` + `.commit()` | Reemplazar un Fragment por otro |

**Anterior:** [← Capítulo 9 — Formularios](09_formularios_validacion.md) | **Siguiente:** [Capítulo 11 — Retrofit →](11_retrofit_api.md)
