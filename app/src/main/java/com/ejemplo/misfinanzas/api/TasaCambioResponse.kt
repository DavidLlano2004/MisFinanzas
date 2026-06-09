package com.ejemplo.misfinanzas.api

data class TasaCambioResponse(
    val base: String,
    val rates: Map<String, Double>
)
