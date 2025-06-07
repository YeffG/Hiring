package com.example.hiringappv1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    private lateinit var btnLogin: Button
    private lateinit var txbCorreoLogin: EditText
    private lateinit var txbClaveLogin: EditText
    private lateinit var sqlite: Sqlite
    private lateinit var txBtnRegistrarse: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnLogin = findViewById(R.id.btnLogin)
        txbCorreoLogin = findViewById(R.id.txbCorreoLogin)
        txbClaveLogin = findViewById(R.id.txbClaveLogin)
        sqlite = Sqlite(this)
        txBtnRegistrarse = findViewById(R.id.txBtnRegistrarse)


        val usuarios = sqlite.obtenerTodosLosDatosDeTablas()
        Log.d("SQLite", "Usuarios: $usuarios")

        val trabajadores = sqlite.obtenerTrabajadores()
        Log.d("SQLite", "Trabajadores: $trabajadores")

        val solicitudes = sqlite.obtenerTodasLasSolicitudes()
        Log.d("SQLite", "Solicitudes: $solicitudes")

        val idPrueba = sqlite.obtenerIdUsuarioPorCorreo("tati@gmail.com")
        Log.d("SQLite", "ID de Tati: $idPrueba")

        val solicitudPrueba = sqlite.obtenerSolicitudesPorTrabajador(idPrueba)
        Log.d("SQLite", "Solicitudes de Tati: $solicitudPrueba")

        txBtnRegistrarse.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val correo = txbCorreoLogin.text.toString().trim()
            val clave = txbClaveLogin.text.toString().trim()

            var esValido = true

            if (correo.isEmpty()) {
                txbCorreoLogin.error = "Campo obligatorio"
                esValido = false
            }

            if (clave.isEmpty()) {
                txbClaveLogin.error = "Campo obligatorio"
                esValido = false
            }

            if (esValido) {
                // Verificar si las credenciales son correctas
                if (sqlite.validarUsuario(correo, clave)) {

                    val tipoUsuario = sqlite.obtenerTipoUsuarioPorCorreo(correo)

                    if (tipoUsuario != null) {
                        val intent = when (tipoUsuario) {
                            "Trabajador" -> {
                                Intent(this, InicioTrabajadorActivity::class.java)
                            }

                            "Cliente" -> {
                                Intent(this, Inicio::class.java)
                            }
                            else -> {
                                Log.e("LoginActivity", "Tipo de usuario desconocido: $tipoUsuario")
                                null
                            }
                        }
                        if (intent != null) {
                            intent.putExtra("CorreoLogin", correo)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Tipo de usuario desconocido", Toast.LENGTH_LONG)
                                .show()
                        }
                    } else {
                        // Credenciales incorrectas
                        Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------------------------

//// Registro para Luis (ID 2)
//        val resultadoLuis = sqlite.insertarTrabajador(2, "15/03/1990", "Calle 10 #20-30, Medellín", "Electricista", "5 años de experiencia en instalaciones residenciales")
//        if (resultadoLuis) {
//            println("Luis insertado correctamente.")
//        } else {
//            println("Error al insertar a Luis. Revisa los logs para más detalles.")
//        }
//
//// ---
//
//// Registro para Tati (ID 3)
//        val resultadoTati = sqlite.insertarTrabajador(3, "22/07/1985", "Avenida Siempre Viva 123, Envigado", "Fontanera", "10 años trabajando en reparaciones e instalaciones de tuberías")
//        if (resultadoTati) {
//            println("Tati insertada correctamente.")
//        } else {
//            println("Error al insertar a Tati. Revisa los logs para más detalles.")
//        }
//
//// ---
//
//// Registro para Gabriel (ID 4)
//        val resultadoGabriel = sqlite.insertarTrabajador(4, "01/11/1992", "Carrera 50 #45-60, Itagüí", "Albañil", "8 años de experiencia en construcción y remodelación")
//        if (resultadoGabriel) {
//            println("Gabriel insertado correctamente.")
//        } else {
//            println("Error al insertar a Gabriel. Revisa los logs para más detalles.")
//        }
//
//// ---
//
//// Registro para Yo (ID 5)
//        val resultadoYo = sqlite.insertarTrabajador(5, "04/09/1988", "Diagonal 75 #30-15, Sabaneta", "Jardinero", "6 años en diseño y mantenimiento de jardines")
//        if (resultadoYo) {
//            println("Yo insertado correctamente.")
//        } else {
//            println("Error al insertar a Yo. Revisa los logs para más detalles.")
//        }
//--