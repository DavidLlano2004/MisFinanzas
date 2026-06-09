package com.ejemplo.misfinanzas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ejemplo.misfinanzas.api.ResultadoApi
import com.ejemplo.misfinanzas.api.TasaCambioResponse
import com.ejemplo.misfinanzas.modelo.Transaccion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransaccionRepository

    val transacciones: LiveData<List<Transaccion>>
    val balance: LiveData<Double>
    val ingresos: LiveData<Double>
    val gastos: LiveData<Double>
    val cantidad: LiveData<Int>

    private val _tasasCambio = MutableLiveData<ResultadoApi<TasaCambioResponse>>()
    val tasasCambio: LiveData<ResultadoApi<TasaCambioResponse>> = _tasasCambio

    init {
        val dao = AppDatabase.obtenerInstancia(application).transaccionDao()
        repository    = TransaccionRepository(dao)
        transacciones = repository.todasLasTransacciones
        balance       = repository.balance
        ingresos      = repository.totalIngresos
        gastos        = repository.totalGastos
        cantidad      = repository.cantidad
    }

    fun agregarTransaccion(transaccion: Transaccion) {
        viewModelScope.launch { repository.insertar(transaccion) }
    }

    fun eliminarTransaccion(transaccion: Transaccion) {
        viewModelScope.launch { repository.eliminar(transaccion) }
    }

    fun insertarDatosDePrueba() {
        viewModelScope.launch { repository.insertarTodas(Transaccion.datosDePrueba()) }
    }

    fun eliminarTodas() {
        viewModelScope.launch { repository.eliminarTodas() }
    }

    fun consultarTasas(monedaBase: String = "USD") {
        _tasasCambio.value = ResultadoApi.Cargando
        viewModelScope.launch {
            try {
                val respuesta = withContext(Dispatchers.IO) {
                    repository.obtenerTasasCambio(monedaBase)
                }
                _tasasCambio.value = ResultadoApi.Exito(respuesta)
            } catch (e: UnknownHostException) {
                _tasasCambio.value = ResultadoApi.Error("Sin conexión a internet")
            } catch (e: SocketTimeoutException) {
                _tasasCambio.value = ResultadoApi.Error("Tiempo de espera agotado")
            } catch (e: Exception) {
                _tasasCambio.value = ResultadoApi.Error("Error: ${e.message}")
            }
        }
    }
}
