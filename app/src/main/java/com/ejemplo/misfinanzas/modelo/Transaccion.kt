package com.ejemplo.misfinanzas.modelo

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Locale

@Entity(tableName = "transacciones")
data class Transaccion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val monto: Double,
    val descripcion: String,
    val categoriaNombre: String,
    val fecha: String = "Hoy"
) : Serializable {

    val esIngreso: Boolean get() = monto > 0

    val categoria: Categoria
        get() = try {
            Categoria.valueOf(categoriaNombre)
        } catch (e: IllegalArgumentException) {
            Categoria.OTROS
        }

    fun montoFormateado(): String {
        val signo = if (esIngreso) "+" else "-"
        return "$signo $ ${String.format(Locale.US, "%,.0f", Math.abs(monto))}"
    }

    companion object {
        fun datosDePrueba(): List<Transaccion> = listOf(
            Transaccion(monto =  2500000.0, descripcion = "Salario mensual",       categoriaNombre = Categoria.SALARIO.name),
            Transaccion(monto =   500000.0, descripcion = "Proyecto web",           categoriaNombre = Categoria.FREELANCE.name,       fecha = "Ayer"),
            Transaccion(monto =  -150000.0, descripcion = "Mercado semanal",        categoriaNombre = Categoria.COMIDA.name),
            Transaccion(monto =   -80000.0, descripcion = "Tarjeta metro",          categoriaNombre = Categoria.TRANSPORTE.name,      fecha = "Ayer"),
            Transaccion(monto =  -200000.0, descripcion = "Agua y luz",             categoriaNombre = Categoria.SERVICIOS.name,       fecha = "Hace 2 días"),
            Transaccion(monto =   -50000.0, descripcion = "Netflix",                categoriaNombre = Categoria.ENTRETENIMIENTO.name, fecha = "Hace 3 días"),
            Transaccion(monto =   -35000.0, descripcion = "Medicinas",              categoriaNombre = Categoria.SALUD.name,           fecha = "Hace 4 días"),
            Transaccion(monto =  -120000.0, descripcion = "Almuerzo restaurante",   categoriaNombre = Categoria.COMIDA.name,          fecha = "Hace 4 días")
        )
    }
}
