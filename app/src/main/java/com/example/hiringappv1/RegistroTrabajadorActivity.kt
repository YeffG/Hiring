package com.example.hiringappv1

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import android.widget.Button
import android.widget.EditText
import android.content.Intent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistroTrabajadorActivity : AppCompatActivity() {
    private lateinit var btnAceptar: Button
    private lateinit var txbFechaNacimiento: EditText
    private lateinit var txbDireccion: EditText
    private lateinit var txbOficioRegistro: EditText
    private lateinit var txbExperiencia: TextView
    private lateinit var sqlite: Sqlite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.registro_trabajador)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Inicializar componentes
        btnAceptar = findViewById(R.id.btnAceptar)
        txbFechaNacimiento = findViewById(R.id.txbFechaNacimiento)
        txbDireccion = findViewById(R.id.txbDireccion)
        txbOficioRegistro = findViewById(R.id.txbOficioRegistro)
        txbExperiencia = findViewById(R.id.txbExperiencia)
        sqlite = Sqlite(this)

//        val correo = intent.getStringExtra("correo")
//        val id = sqlite.obtenerIdUsuarioPorCorreo(correo.toString())
        val correo = intent.getStringExtra("CorreoLogin")
        var id: Int = -1 // Or some other default/invalid ID
        id = sqlite.obtenerIdUsuarioPorCorreo(correo.toString()) ?: -1



        btnAceptar.setOnClickListener {
            if (correo.isNullOrEmpty()) {
                Toast.makeText(this, "Error: no se recibió el correo del usuario", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val fechaNacimiento = txbFechaNacimiento.text.toString()
            val direccion = txbDireccion.text.toString()
            val oficio = txbOficioRegistro.text.toString()
            val experiencia = txbExperiencia.text.toString()

            var esValido = true

            if (fechaNacimiento.isEmpty()) {
                txbFechaNacimiento.error = "Campo obligatorio"
                esValido = false
            }

            if (direccion.isEmpty()) {
                txbDireccion.error = "Campo obligatorio"
                esValido = false
            }

            if (oficio.isEmpty()) {
                txbOficioRegistro.error = "Campo obligatorio"
                esValido = false
            }

            if (experiencia.isEmpty()) {
                txbExperiencia.error = "Campo obligatorio"
                esValido = false
            }

            val registrado = sqlite.insertarTrabajador(id, fechaNacimiento, direccion, oficio, experiencia)

            if(registrado){
                Toast.makeText(this, "Datos del trabajador registrados correctamente", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, InicioTrabajadorActivity::class.java)
                intent.putExtra("CorreoLogin", correo)
                startActivity(intent)
            }else{
                Toast.makeText(this, "Error al registrar los datos del trabajador", Toast.LENGTH_SHORT).show()
            }

        }
    }
}