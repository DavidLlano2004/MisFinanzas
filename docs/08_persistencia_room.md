# Capítulo 8: Persistencia con Room

## Objetivo

Guardar las transacciones en una base de datos SQLite local usando Room. Los datos sobrevivirán a cierres de la app.

---

## 8.1 ¿Qué es Room?

Room es una librería de Google que simplifica SQLite en Android usando anotaciones Kotlin. Es el equivalente a `sqflite` en Flutter.

```
Tu código Kotlin
     ↓ anotaciones (@Entity, @Dao, @Database)
   Room
     ↓ genera código SQL automáticamente
   SQLite (base de datos del dispositivo)
```

---

## 8.2 Las tres piezas de Room

### 1. Entity: la tabla

```kotlin
@Entity(tableName = "transacciones")
data class Transaccion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val monto: Double,
    val descripcion: String,
    val categoriaNombre: String,  // String en lugar de enum (Room no soporta enums)
    val fecha: String = "Hoy"
) : Serializable {
    // Propiedad calculada — Room NO la guarda (no tiene backing field)
    val categoria: Categoria
        get() = try { Categoria.valueOf(categoriaNombre) }
                catch (e: Exception) { Categoria.OTROS }
}
```

**¿Por qué `categoriaNombre: String` y no `categoria: Categoria`?**
Room guarda tipos primitivos (Int, Double, String). No puede guardar un enum directamente. Guardamos el `name` del enum ("COMIDA", "SALARIO") y lo convertimos en un getter.

### 2. DAO: las consultas

```kotlin
@Dao
interface TransaccionDao {

    @Query("SELECT * FROM transacciones ORDER BY id DESC")
    fun obtenerTodas(): LiveData<List<Transaccion>>  // LiveData = actualización automática

    @Query("SELECT COALESCE(SUM(monto), 0.0) FROM transacciones")
    fun obtenerBalance(): LiveData<Double>

    @Insert
    suspend fun insertar(transaccion: Transaccion)

    @Delete
    suspend fun eliminar(transaccion: Transaccion)
}
```

### 3. Database: el conector

```kotlin
@Database(entities = [Transaccion::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transaccionDao(): TransaccionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun obtenerInstancia(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "misfinanzas_db")
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}
```

---

## 8.3 Usar Room desde el ViewModel

Las operaciones de escritura deben ir en coroutines (no en el hilo principal):

```kotlin
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.obtenerInstancia(application).transaccionDao()

    val transacciones: LiveData<List<Transaccion>> = dao.obtenerTodas()

    fun agregarTransaccion(t: Transaccion) {
        viewModelScope.launch { dao.insertar(t) }  // viewModelScope = hilo IO automático
    }
}
```

---

## 8.4 Dependencias + KSP

```kotlin
// build.gradle.kts (root)
id("com.google.devtools.ksp") version "1.9.24-1.0.20" apply false

// app/build.gradle.kts
plugins { id("com.google.devtools.ksp") }
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")
```

KSP (Kotlin Symbol Processing) procesa las anotaciones de Room en tiempo de compilación y genera el código SQL automáticamente.

---

## Resumen

| Concepto | Para qué |
|----------|----------|
| `@Entity` | Marca una data class como tabla de SQLite |
| `@PrimaryKey(autoGenerate = true)` | ID único auto-generado |
| `@Dao` | Interfaz con las consultas SQL |
| `@Database` | Conecta entidades con DAOs |
| `LiveData<List<T>>` del DAO | La UI se actualiza automáticamente al cambiar datos |
| `suspend fun` en DAO | Operaciones de escritura fuera del hilo principal |
| `viewModelScope.launch` | Ejecutar coroutines ligadas al ViewModel |

**Anterior:** [← Capítulo 7 — ViewModel](07_ciclo_vida_viewmodel.md) | **Siguiente:** [Capítulo 9 — Formularios →](09_formularios_validacion.md)
