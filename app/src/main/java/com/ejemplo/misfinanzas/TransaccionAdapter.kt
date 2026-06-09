package com.ejemplo.misfinanzas

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ejemplo.misfinanzas.databinding.ItemTransaccionBinding
import com.ejemplo.misfinanzas.modelo.Transaccion

class TransaccionAdapter(
    private val transacciones: List<Transaccion>,
    private val onItemClick: (Transaccion) -> Unit
) : RecyclerView.Adapter<TransaccionAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemTransaccionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransaccionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val t = transacciones[position]
        val ctx = holder.itemView.context

        holder.binding.tvEmoji.text = t.categoria.emoji
        holder.binding.tvDescripcion.text = t.descripcion
        holder.binding.tvCategoria.text = t.categoria.etiqueta
        holder.binding.tvFecha.text = t.fecha
        holder.binding.tvMonto.text = t.montoFormateado()

        // Badge del monto: verde para ingresos, rojo para gastos
        if (t.esIngreso) {
            holder.binding.tvMonto.setTextColor(ctx.getColor(R.color.verde_primario))
            holder.binding.tvMonto.setBackgroundResource(R.drawable.bg_monto_ingreso)
        } else {
            holder.binding.tvMonto.setTextColor(ctx.getColor(R.color.rojo_gasto))
            holder.binding.tvMonto.setBackgroundResource(R.drawable.bg_monto_gasto)
        }

        // Fondo del ícono con el color de la categoría (mutate evita compartir estado)
        val bg = holder.binding.flIconBg.background.mutate() as? GradientDrawable
        bg?.setColor(Color.parseColor(t.categoria.colorFondoHex))

        holder.itemView.setOnClickListener { onItemClick(t) }
    }

    override fun getItemCount(): Int = transacciones.size
}
