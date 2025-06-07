package com.example.hiringappv1

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Inicio : AppCompatActivity() {

    private lateinit var NombreInicio: TextView
    private lateinit var sqlite: Sqlite
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var adapter: TrabajadorAdapter
    private lateinit var emptyMessage: TextView

    fun formatearNombre(nombre: String): String {
        return nombre.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.inicio)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        NombreInicio = findViewById(R.id.NombreInicio)
        searchEditText = findViewById(R.id.txbBuscar)
        recyclerView = findViewById(R.id.recyclerViewWorkers)
        emptyMessage = findViewById(R.id.textNoResults)

        sqlite = Sqlite(this)

        val correo = intent.getStringExtra("CorreoLogin")
        if (correo != null) {
            val nombre = sqlite.obtenerNombrePorCorreo(correo)
            NombreInicio.text = if (nombre != null) formatearNombre(nombre) else ""
        }

        // Cargar trabajadores desde la base de datos
        val listaTrabajadores = sqlite.obtenerTrabajadores() // Asegúrate que este método exista

        adapter = TrabajadorAdapter(listaTrabajadores) { trabajador ->
//             Clic en un trabajador: navegar al detalle
            val intent = Intent(this, ProfileDetailActivity::class.java)
            intent.putExtra("TRABAJADOR_ID", trabajador.id)
            intent.putExtra("CorreoLogin", correo)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        toggleResultsView(adapter.hasResults())

        // Filtrar en tiempo real
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
                toggleResultsView(adapter.hasResults())
            }
        })
    }
    private fun toggleResultsView(hasResults: Boolean) {
        recyclerView.visibility = if (hasResults) View.VISIBLE else View.GONE
        emptyMessage.visibility = if (hasResults) View.GONE else View.VISIBLE
    }
}
