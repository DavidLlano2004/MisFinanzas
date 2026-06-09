package com.ejemplo.misfinanzas

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ejemplo.misfinanzas.databinding.FragmentTransaccionesBinding
import com.ejemplo.misfinanzas.modelo.Transaccion
import com.google.android.material.snackbar.Snackbar

class TransaccionesFragment : Fragment() {

    private var _binding: FragmentTransaccionesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    private val lanzarAgregar = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        if (resultado.resultCode == Activity.RESULT_OK) {
            val nueva = resultado.data?.getSerializableExtra("NUEVA_TRANSACCION") as? Transaccion
            if (nueva != null) {
                viewModel.agregarTransaccion(nueva)
                Toast.makeText(requireContext(), "Transacción guardada ✓", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransaccionesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvTransacciones.layoutManager = LinearLayoutManager(requireContext())

        viewModel.transacciones.observe(viewLifecycleOwner) { lista ->
            binding.rvTransacciones.adapter = TransaccionAdapter(lista) { t ->
                Toast.makeText(
                    requireContext(),
                    "${t.descripcion}: ${t.montoFormateado()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            binding.tvCantidad.text = "${lista.size} movimientos"
        }

        configurarSwipeEliminar()

        binding.btnAgregar.setOnClickListener {
            lanzarAgregar.launch(Intent(requireContext(), AgregarActivity::class.java))
        }
    }

    private fun configurarSwipeEliminar() {
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                rv: RecyclerView,
                vh: RecyclerView.ViewHolder,
                t: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val lista = viewModel.transacciones.value ?: return
                val pos   = viewHolder.adapterPosition
                if (pos < 0 || pos >= lista.size) return
                val transaccion = lista[pos]
                viewModel.eliminarTransaccion(transaccion)

                Snackbar.make(binding.root, "Eliminada", Snackbar.LENGTH_LONG)
                    .setAction("Deshacer") { viewModel.agregarTransaccion(transaccion) }
                    .show()
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.rvTransacciones)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
