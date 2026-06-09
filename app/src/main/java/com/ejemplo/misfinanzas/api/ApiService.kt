package com.ejemplo.misfinanzas.api

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("latest")
    suspend fun obtenerTasas(
        @Query("base") monedaBase: String = "USD"
    ): TasaCambioResponse
}
