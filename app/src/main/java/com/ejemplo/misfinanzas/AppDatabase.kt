package com.ejemplo.misfinanzas

import android.content.Context
import androidx.room.*
import com.ejemplo.misfinanzas.modelo.Transaccion

@Database(entities = [Transaccion::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transaccionDao(): TransaccionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun obtenerInstancia(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "misfinanzas_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instancia
                instancia
            }
        }
    }
}
