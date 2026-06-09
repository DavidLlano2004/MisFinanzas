package com.ejemplo.misfinanzas

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.ejemplo.misfinanzas.databinding.ActivityAgregarBinding
import com.ejemplo.misfinanzas.modelo.Categoria
import com.ejemplo.misfinanzas.modelo.Transaccion

class AgregarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarBinding
    private var tipoIngreso = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarBotones()
        actualizarCategorias()
        configurarValidacion()
    }

    private fun configurarBotones() {
        actualizarAparienciaBotones()

        binding.btnGasto.setOnClickListener {
            tipoIngreso = false
            actualizarAparienciaBotones()
            actualizarCategorias()
        }

        binding.btnIngreso.setOnClickListener {
            tipoIngreso = true
            actualizarAparienciaBotones()
            actualizarCategorias()
        }

        binding.btnCancelar.setOnClickListener { finish() }

        binding.btnGuardar.setOnClickListener { guardar() }
    }

    private fun actualizarAparienciaBotones() {
        binding.btnGasto.alpha   = if (!tipoIngreso) 1f else 0.4f
        binding.btnIngreso.alpha = if (tipoIngreso) 1f else 0.4f
    }

    private fun actualizarCategorias() {
        val cats = if (tipoIngreso) {
            Categoria.values().filter { it.esIngreso() }
        } else {
            Categoria.values().filter { !it.esIngreso() }
        }
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            cats.map { "${it.emoji} ${it.etiqueta}" }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCategoria.adapter = adapter
        binding.spCategoria.tag = cats
    }

    private fun configurarValidacion() {
        binding.etMonto.doAfterTextChanged { validarFormulario() }
        binding.etDescripcion.doAfterTextChanged { validarFormulario() }
        validarFormulario()
    }

    private fun validarFormulario() {
        val montoTxt = binding.etMonto.text?.toString() ?: ""
        val descTxt  = binding.etDescripcion.text?.toString() ?: ""

        val montoValido = montoTxt.isNotBlank()
            && montoTxt.toDoubleOrNull() != null
            && montoTxt.toDouble() > 0
        val descValida = descTxt.trim().length >= 3

        binding.tilMonto.error = when {
            montoTxt.isBlank()               -> null
            montoTxt.toDoubleOrNull() == null -> "Ingresa un número válido"
            montoTxt.toDouble() <= 0          -> "El monto debe ser mayor a 0"
            else                              -> null
        }

        binding.tilDescripcion.error = when {
            descTxt.isBlank()           -> null
            descTxt.trim().length < 3   -> "Mínimo 3 caracteres"
            else                        -> null
        }

        binding.btnGuardar.isEnabled = montoValido && descValida
        binding.btnGuardar.alpha     = if (montoValido && descValida) 1f else 0.5f
    }

    private fun guardar() {
        val montoRaw  = binding.etMonto.text.toString().toDouble()
        val monto     = if (tipoIngreso) montoRaw else -montoRaw
        val descripcion = binding.etDescripcion.text.toString().trim()

        @Suppress("UNCHECKED_CAST")
        val cats = (binding.spCategoria.tag as? List<Categoria>) ?: Categoria.values().toList()
        val categoria = cats.getOrElse(binding.spCategoria.selectedItemPosition) { Categoria.OTROS }

        val nueva = Transaccion(
            monto = monto,
            descripcion = descripcion,
            categoriaNombre = categoria.name
        )

        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra("NUEVA_TRANSACCION", nueva)
        })
        finish()
    }
}
