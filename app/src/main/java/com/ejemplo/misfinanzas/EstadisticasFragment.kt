package com.ejemplo.misfinanzas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ejemplo.misfinanzas.api.ResultadoApi
import com.ejemplo.misfinanzas.databinding.FragmentEstadisticasBinding
import com.ejemplo.misfinanzas.modelo.Transaccion

class EstadisticasFragment : Fragment() {

    private var _binding: FragmentEstadisticasBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEstadisticasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.transacciones.observe(viewLifecycleOwner) { lista ->
            mostrarEstadisticas(lista)
        }

        viewModel.tasasCambio.observe(viewLifecycleOwner) { resultado ->
            when (resultado) {
                is ResultadoApi.Cargando -> {
                    binding.progressTasas.visibility = View.VISIBLE
                    binding.tvTasas.text = ""
                }
                is ResultadoApi.Exito -> {
                    binding.progressTasas.visibility = View.GONE
                    val tasas = resultado.datos.rates
                    binding.tvTasas.text = buildString {
                        appendLine("1 USD = ${tasas["COP"] ?: "N/A"} COP")
                        appendLine("1 USD = ${tasas["EUR"] ?: "N/A"} EUR")
                        appendLine("1 USD = ${tasas["GBP"] ?: "N/A"} GBP")
                        appendLine("1 USD = ${tasas["BRL"] ?: "N/A"} BRL")
                        append("1 USD = ${tasas["MXN"] ?: "N/A"} MXN")
                    }
                }
                is ResultadoApi.Error -> {
                    binding.progressTasas.visibility = View.GONE
                    binding.tvTasas.text = resultado.mensaje
                    binding.tvTasas.setTextColor(requireContext().getColor(R.color.rojo_gasto))
                }
            }
        }

        binding.btnConsultarTasas.setOnClickListener {
            viewModel.consultarTasas()
        }
    }

    private fun mostrarEstadisticas(transacciones: List<Transaccion>) {
        val gastos = transacciones.filter { !it.esIngreso }

        val topGastos = gastos
            .groupBy { it.categoria }
            .mapValues { (_, lista) -> lista.sumOf { Math.abs(it.monto) } }
            .toList()
            .sortedByDescending { it.second }
            .take(3)

        val resumen = buildString {
            if (topGastos.isEmpty()) {
                appendLine("Sin transacciones aún.")
            } else {
                appendLine("Top categorías de gasto:")
                topGastos.forEachIndexed { idx, (cat, total) ->
                    appendLine("${idx + 1}. ${cat.emoji} ${cat.etiqueta}: ${total.formatearCOP()}")
                }
            }
            appendLine()
            transacciones
                .filter { it.esIngreso }
                .maxByOrNull { it.monto }
                ?.let { append("Mayor ingreso: ${it.descripcion} (${it.montoFormateado()})") }
        }

        binding.tvDetalleCategorias.text = resumen
        binding.tvTotalTransacciones.text = "Total de transacciones: ${transacciones.size}"

        val promedioGasto = if (gastos.isNotEmpty()) {
            gastos.sumOf { Math.abs(it.monto) } / gastos.size
        } else 0.0
        binding.tvPromedioGasto.text = "Promedio por gasto: ${promedioGasto.formatearCOP()}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
