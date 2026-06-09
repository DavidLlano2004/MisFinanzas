package com.ejemplo.misfinanzas

import androidx.lifecycle.LiveData
import com.ejemplo.misfinanzas.api.RetrofitClient
import com.ejemplo.misfinanzas.api.TasaCambioResponse
import com.ejemplo.misfinanzas.modelo.Transaccion

class TransaccionRepository(private val dao: TransaccionDao) {

    // Datos locales (Room) — el ViewModel solo accede a estas propiedades
    val todasLasTransacciones: LiveData<List<Transaccion>> = dao.obtenerTodas()
    val balance: LiveData<Double>                          = dao.obtenerBalance()
    val totalIngresos: LiveData<Double>                   = dao.obtenerTotalIngresos()
    val totalGastos: LiveData<Double>                     = dao.obtenerTotalGastos()
    val cantidad: LiveData<Int>                           = dao.obtenerCantidad()

    suspend fun insertar(transaccion: Transaccion)               = dao.insertar(transaccion)
    suspend fun insertarTodas(transacciones: List<Transaccion>)  = dao.insertarTodas(transacciones)
    suspend fun eliminar(transaccion: Transaccion)               = dao.eliminar(transaccion)
    suspend fun eliminarTodas()                                  = dao.eliminarTodas()

    // Datos remotos (Retrofit)
    suspend fun obtenerTasasCambio(monedaBase: String = "USD"): TasaCambioResponse =
        RetrofitClient.apiService.obtenerTasas(monedaBase)
}
