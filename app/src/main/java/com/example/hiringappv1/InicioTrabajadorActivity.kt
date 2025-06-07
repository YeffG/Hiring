package com.example.hiringappv1

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InicioTrabajadorActivity : AppCompatActivity() {

    private lateinit var nombreInicio: TextView
    private lateinit var searchEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SolicitudAdapter
    private lateinit var sqlite: Sqlite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.inicio_trabajador)

        // Ajuste de padding por barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar vistas
        nombreInicio = findViewById(R.id.NombreInicio)
        searchEditText = findViewById(R.id.txbBuscar)
        recyclerView = findViewById(R.id.recyclerSolicitudes)

        // Inicializar SQLite
        sqlite = Sqlite(this)

        val correoLogin = intent.getStringExtra("CorreoLogin") // Usa un nombre de variable diferente para claridad
        Log.d("InicioTrabajadorActivity", "CorreoLogin recibido del Intent: $correoLogin")

        var idTrabajadorParaSolicitudes: Int = -1 // Por defecto, un ID inválido

        if (correoLogin.isNullOrEmpty()) {
            Log.e("InicioTrabajadorActivity", "Error: CorreoLogin es nulo o vacío. No se puede identificar al trabajador.")
            nombreInicio.text = "Usuario" // O alguna indicación de error
            // idTrabajadorParaSolicitudes permanece -1, resultará en lista vacía de solicitudes
        } else {
            // El correoLogin tiene un valor, intenta obtener el nombre y el ID
            nombreInicio.text = sqlite.obtenerNombrePorCorreo(correoLogin)?.let { nombreCompleto ->
                formatearNombre(nombreCompleto)
            } ?: "Usuario" // Si obtenerNombrePorCorreo falla, usa "Usuario"

            idTrabajadorParaSolicitudes = sqlite.obtenerIdUsuarioPorCorreo(correoLogin) ?: -1
            if (idTrabajadorParaSolicitudes == -1) {
                Log.w("InicioTrabajadorActivity", "No se encontró ID para el correo: $correoLogin. Se usará ID -1.")
            }
        }
        Log.d("InicioTrabajadorActivity", "ID del trabajador a usar para solicitudes: $idTrabajadorParaSolicitudes")

        val listaSolicitudes = sqlite.obtenerSolicitudesPorTrabajador(idTrabajadorParaSolicitudes)
        Log.d("InicioTrabajadorActivity", "Número de solicitudes obtenidas: ${listaSolicitudes.size}")



        val sqlite = Sqlite(this) // Asegúrate de tener contexto válido
        adapter = SolicitudAdapter(
            listaSolicitudes,
            onItemClick = { solicitudInfo ->
                Toast.makeText(this, "Solicitud de ${solicitudInfo.nombreCliente}", Toast.LENGTH_SHORT).show()
            },
            sqlite = sqlite,
            onActualizarLista = {
                cargarSolicitudesDesdeDB(idTrabajadorParaSolicitudes)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Buscar mientras escribe
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }
        })
    }

    private fun formatearNombre(nombre: String): String {
        return nombre.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    fun cargarSolicitudesDesdeDB(idTrabajador: Int) {
        val nuevasSolicitudes = sqlite.obtenerSolicitudesPorTrabajador(idTrabajador)
        adapter.updateList(nuevasSolicitudes)
    }
}
