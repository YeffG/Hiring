package com.example.hiringappv1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hiringappv1.R

class TrabajadorAdapter(
    private var workerList: List<DataClassesTrabajador>,
    private val onItemClick: (DataClassesTrabajador) -> Unit
) : RecyclerView.Adapter<TrabajadorAdapter.WorkerViewHolder>() {

    private var filteredList = workerList.toMutableList()

    inner class WorkerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textNombre)
        val professionTextView: TextView = itemView.findViewById(R.id.textServicio)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(filteredList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_worker, parent, false)
        return WorkerViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkerViewHolder, position: Int) {
        val worker = filteredList[position]
        holder.nameTextView.text = formatearNombre(worker.nombre)
        holder.professionTextView.text = formatearNombre(worker.oficio)
    }

    override fun getItemCount(): Int = filteredList.size

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            workerList.toMutableList()
        } else {
            workerList.filter {
                it.nombre.contains(query, ignoreCase = true) || it.oficio.contains(query, ignoreCase = true)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    fun updateList(newList: List<DataClassesTrabajador>) {
        workerList = newList
        filter("") // refresh filtered list
    }
    fun formatearNombre(nombre: String): String {
        return nombre.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    fun hasResults(): Boolean = filteredList.isNotEmpty()
}
