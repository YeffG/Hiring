package com.example.hiringappv1

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.content.Intent
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {
    private lateinit var btnRegistrar: Button
    private lateinit var txbNombre: EditText
    private lateinit var txbCorreoRegistro: EditText
    private lateinit var txbClaveRegistro: EditText
    private lateinit var listTipoUsuario: Spinner
    private lateinit var sqlite: Sqlite
    private lateinit var txbLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar componentes
        btnRegistrar = findViewById(R.id.btnRegistrar)
        txbNombre = findViewById(R.id.txbNombre)
        txbCorreoRegistro = findViewById(R.id.txbCorreoRegistro)
        txbClaveRegistro = findViewById(R.id.txbClaveRegistro)
        listTipoUsuario = findViewById(R.id.listTipoUsuario)
        sqlite = Sqlite(this)
        txbLogin = findViewById(R.id.txbLogin)

        txbLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val opciones = listOf("Tipo de usuario","Cliente", "Trabajador")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        listTipoUsuario.adapter = adapter

        val correoRecibido = intent.getStringExtra("correo")
        if (!correoRecibido.isNullOrEmpty()) {
            txbCorreoRegistro.setText(correoRecibido)
        }

        btnRegistrar.setOnClickListener {
            val nombre = txbNombre.text.toString()
            val correo = txbCorreoRegistro.text.toString()
            val clave = txbClaveRegistro.text.toString()
            val tipoUsuario = listTipoUsuario.selectedItem.toString()

            var esValido = true

            if (correo.isEmpty()) {
                txbCorreoRegistro.error = "Campo obligatorio"
                esValido = false
            }

            if (clave.isEmpty()) {
                txbClaveRegistro.error = "Campo obligatorio"
                esValido = false
            }

            if (nombre.isEmpty()) {
                txbNombre.error = "Campo obligatorio"
                esValido = false
            }

            if (esValido && tipoUsuario != "Tipo de usuario") {
                if (!sqlite.existeUsuario(correo)) {
                    val registrado = sqlite.insertarPUsuario(nombre, correo, clave, tipoUsuario)
                    Log.d("Registro","Registrado: ${registrado}")
                    if(registrado){
                        if(tipoUsuario == "Trabajador"){
                            Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, RegistroTrabajadorActivity::class.java)
                            intent.putExtra("CorreoLogin", correo)
                            startActivity(intent)
                            Log.d("Registro","Usuario Trabajador. :{-pñl  ${sqlite.existeUsuario(correo)}")
                        }else{
                            sqlite.insertarCliente(correo)
                            Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, Inicio::class.java)
                            intent.putExtra("CorreoLogin", correo)
                            startActivity(intent)
                            Log.d("Registro","Usuario Cliente: ${sqlite.existeUsuario(correo)}")
                        }
                        finish()
                    }else{
                        Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this, "Error: El correo ya está registrado", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("Registro","Usuario ya registrado: ${sqlite.existeUsuario(correo)}")
                }
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}