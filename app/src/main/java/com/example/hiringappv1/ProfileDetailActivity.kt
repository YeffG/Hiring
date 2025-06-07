package com.example.hiringappv1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileDetailActivity : AppCompatActivity() {
    private lateinit var sqlite: Sqlite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.perfil)

        val trabajadorId = intent.getIntExtra("TRABAJADOR_ID", -1)
        val correo = intent.getStringExtra("CorreoLogin")
        sqlite = Sqlite(this)
        val idCliente: Int = sqlite.obtenerIdUsuarioPorCorreo(correo.toString()) ?: -1

        fun formatearNombre(nombre: String): String {
            return nombre.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
        val trabajador = sqlite.obtenerTrabajadorPorId(trabajadorId).firstOrNull()

        if (trabajador != null) {
            findViewById<TextView>(R.id.tvNombre).text = formatearNombre(trabajador.nombre)
            findViewById<TextView>(R.id.tvOficio).text = formatearNombre(trabajador.oficio)
            findViewById<TextView>(R.id.tvDireccion).text = formatearNombre(trabajador.direccion)
            findViewById<TextView>(R.id.tvExperiencia).text = formatearNombre(trabajador.experiencia)
        } else {
            // Podrías mostrar un mensaje de error aquí
        }

        findViewById<Button>(R.id.botonSolicitarServicio).setOnClickListener {
            val estado = "en progreso"
            val solicitud = sqlite.insertarSolicitud(estado, idCliente, trabajadorId)
            if (solicitud){
                Toast.makeText(this, "Solicitud enviada correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Toast.makeText(this, "Error al enviar la solicitud", Toast.LENGTH_SHORT).show()
            }
        }
    }
}