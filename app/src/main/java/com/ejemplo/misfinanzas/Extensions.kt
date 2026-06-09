package com.ejemplo.misfinanzas

import android.app.Activity
import android.view.View
import android.widget.Toast

fun Activity.mostrarToast(mensaje: String) {
    Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
}

fun View.mostrar()    { visibility = View.VISIBLE   }
fun View.ocultar()    { visibility = View.GONE       }
fun View.invisible()  { visibility = View.INVISIBLE  }

fun Double.formatearCOP(): String =
    "$ ${String.format("%,.0f", this)}"

fun Double.formatearConSigno(): String {
    val signo = if (this >= 0) "+" else ""
    return "$signo$ ${String.format("%,.0f", this)}"
}
