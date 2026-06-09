package com.ejemplo.misfinanzas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ejemplo.misfinanzas.databinding.FragmentInicioBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InicioFragment : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarMes()
        binding.rvUltimas.layoutManager = LinearLayoutManager(requireContext())

        viewModel.balance.observe(viewLifecycleOwner) { balance ->
            binding.tvBalance.text = (balance ?: 0.0).formatearCOP()
        }

        viewModel.ingresos.observe(viewLifecycleOwner) { ingresos ->
            binding.tvIngresos.text = (ingresos ?: 0.0).formatearCOP()
        }

        viewModel.gastos.observe(viewLifecycleOwner) { gastos ->
            binding.tvGastos.text = (gastos ?: 0.0).formatearCOP()
        }

        viewModel.transacciones.observe(viewLifecycleOwner) { lista ->
            val ingresos = lista.filter { it.esIngreso }.sumOf { it.monto }
            val balance  = lista.sumOf { it.monto }
            val ahorro   = if (ingresos > 0) ((balance / ingresos) * 100).toInt() else 0

            binding.tvPorcentajeAhorro.text  = "$ahorro%"
            binding.tvNumTransacciones.text  = "${lista.size}"

            binding.rvUltimas.adapter = TransaccionAdapter(lista.take(5)) {}
        }
    }

    private fun configurarMes() {
        val fmt = SimpleDateFormat("MMMM yyyy", Locale("es", "CO"))
        binding.tvMes.text = fmt.format(Date()).replaceFirstChar { it.uppercase() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
