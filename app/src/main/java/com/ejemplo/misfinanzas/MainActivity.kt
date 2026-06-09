package com.ejemplo.misfinanzas

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ejemplo.misfinanzas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            cargarFragment(InicioFragment())
        }

        viewModel.transacciones.observe(this) { lista ->
            if (lista.isEmpty()) {
                viewModel.insertarDatosDePrueba()
            }
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio        -> cargarFragment(InicioFragment())
                R.id.nav_transacciones -> cargarFragment(TransaccionesFragment())
                R.id.nav_estadisticas  -> cargarFragment(EstadisticasFragment())
            }
            true
        }
    }

    private fun cargarFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
