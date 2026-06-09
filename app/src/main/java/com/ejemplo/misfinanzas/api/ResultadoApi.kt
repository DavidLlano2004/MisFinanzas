package com.ejemplo.misfinanzas.api

sealed class ResultadoApi<out T> {
    object Cargando : ResultadoApi<Nothing>()
    data class Exito<T>(val datos: T) : ResultadoApi<T>()
    data class Error(val mensaje: String) : ResultadoApi<Nothing>()
}
