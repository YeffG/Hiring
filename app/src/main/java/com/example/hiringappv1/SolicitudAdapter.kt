package com.example.hiringappv1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filterable
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.util.logging.Filter

class SolicitudAdapter(
    private var solicitudList: List<SolicitudInfo>,
    private val onItemClick: (SolicitudInfo) -> Unit,
    private val sqlite: Sqlite,
    private val onActualizarLista: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var filteredList = solicitudList.toMutableList()

    companion object {
        private const val VIEW_TYPE_SOLICITUD = 1
        private const val VIEW_TYPE_EMPTY = 2
    }

    // ViewHolder para solicitudes normales
    inner class SolicitudViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtCliente: TextView = itemView.findViewById(R.id.txtCliente)
        val btnAceptar: Button = itemView.findViewById(R.id.btnAceptar)
        val btnRechazar: Button = itemView.findViewById(R.id.btnRechazar)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION && position < filteredList.size) {
                    onItemClick(filteredList[position])
                }
            }
        }
    }



    // ViewHolder para mensaje de "sin solicitudes"
    inner class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun getItemViewType(position: Int): Int {
        return if (filteredList.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_SOLICITUD
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_EMPTY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_empty_solicitudes, parent, false)
                EmptyViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_solicitudes, parent, false)
                SolicitudViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SolicitudViewHolder -> {
                val solicitud = filteredList[position]
                holder.txtCliente.text = formatearNombre(solicitud.nombreCliente)

                holder.btnAceptar.setOnClickListener {
                    val success = sqlite.actualizarEstadoSolicitud(solicitud.idSolicitud, "aceptado")
                    if (success){
                        Toast.makeText(holder.itemView.context, "Solicitud aceptada", Toast.LENGTH_SHORT).show()
                        onActualizarLista()
                    }
                }

                holder.btnRechazar.setOnClickListener {
                    val success = sqlite.actualizarEstadoSolicitud(solicitud.idSolicitud, "rechazado")
                    if (success){
                        Toast.makeText(holder.itemView.context, "Solicitud Rechazada", Toast.LENGTH_SHORT).show()
                        onActualizarLista()
                    }
                }
            }
            is EmptyViewHolder -> {
                // No hay nada que configurar para el estado vacío
            }
        }
    }

    override fun getItemCount(): Int {
        return if (filteredList.isEmpty()) 1 else filteredList.size
    }

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            solicitudList.toMutableList()
        } else {
            solicitudList.filter {
                it.nombreCliente.contains(query, ignoreCase = true)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    fun updateList(newList: List<SolicitudInfo>) {
        solicitudList = newList
        filter("") // Reset filter
    }

    fun hasResults(): Boolean = filteredList.isNotEmpty()

    private fun formatearNombre(nombre: String): String {
        return nombre.lowercase().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    }
}