package com.ejemplo.misfinanzas

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ejemplo.misfinanzas.modelo.Transaccion

@Dao
interface TransaccionDao {

    @Query("SELECT * FROM transacciones ORDER BY id DESC")
    fun obtenerTodas(): LiveData<List<Transaccion>>

    @Query("SELECT COALESCE(SUM(monto), 0.0) FROM transacciones")
    fun obtenerBalance(): LiveData<Double>

    @Query("SELECT COALESCE(SUM(monto), 0.0) FROM transacciones WHERE monto > 0")
    fun obtenerTotalIngresos(): LiveData<Double>

    @Query("SELECT COALESCE(SUM(ABS(monto)), 0.0) FROM transacciones WHERE monto < 0")
    fun obtenerTotalGastos(): LiveData<Double>

    @Query("SELECT COUNT(*) FROM transacciones")
    fun obtenerCantidad(): LiveData<Int>

    @Insert
    suspend fun insertar(transaccion: Transaccion)

    @Insert
    suspend fun insertarTodas(transacciones: List<Transaccion>)

    @Update
    suspend fun actualizar(transaccion: Transaccion)

    @Delete
    suspend fun eliminar(transaccion: Transaccion)

    @Query("DELETE FROM transacciones")
    suspend fun eliminarTodas()
}
