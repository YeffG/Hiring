package com.example.hiringappv1

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class Sqlite(context: Context) : SQLiteOpenHelper(context, "hiringapp2.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                correo TEXT UNIQUE NOT NULL,
                clave TEXT NOT NULL,
                tipo TEXT NOT NULL
            )
        """)
        db.execSQL("""
            CREATE TABLE clientes (
                id INTEGER PRIMARY KEY,
                FOREIGN KEY(id) REFERENCES usuarios(id) ON DELETE CASCADE
            )
        """)
        db.execSQL("""
            CREATE TABLE trabajadores (
                id INTEGER PRIMARY KEY,
                fechaDeNac TEXT,
                experiencia TEXT, 
                oficio TEXT,
                direccion TEXT,
                FOREIGN KEY(id) REFERENCES usuarios(id) ON DELETE CASCADE
            )
        """)
        db.execSQL("""
            CREATE TABLE solicitud (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                estado TEXT NOT NULL CHECK (estado IN ('aceptado', 'en progreso', 'rechazado', 'terminado')),
                id_cliente INTEGER NOT NULL,
                id_trabajador INTEGER NOT NULL,
                FOREIGN KEY(id_cliente) REFERENCES clientes(id),
                FOREIGN KEY(id_trabajador) REFERENCES trabajadores(id)
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS solicitud")
            db.execSQL("DROP TABLE IF EXISTS trabajadores")
            db.execSQL("DROP TABLE IF EXISTS clientes")
            db.execSQL("DROP TABLE IF EXISTS usuarios")
            onCreate(db)
        }
    }

    // Insertar nuevo usuario
    fun insertarPUsuario(nombre: String, correo: String, contrasena: String, tipo: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("correo", correo)
            put("clave", contrasena)
            put("tipo", tipo)
        }
        val resultado = db.insert("usuarios", null, values)
        Log.e("SQLite", "${resultado}")
        return resultado != -1L
    }

    fun obtenerIdUsuarioPorCorreo(correo: String): Int? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id FROM usuarios WHERE correo = ?", arrayOf(correo))
        val id = if (cursor.moveToFirst()) cursor.getInt(0) else null
        cursor.close()
        return id
    }

    fun obtenerTodosLosDatosDeTablas(): List<DataClassesUsuario> {
        val db = readableDatabase // Asegúrate de que esta variable esté disponible y sea tu instancia de SQLiteDatabase
        val usuarios = mutableListOf<DataClassesUsuario>()
        var cursor: Cursor? = null // Declarar el cursor fuera del try para poder cerrarlo en finally

        try {
            // Usar SELECT * es aceptable aquí si realmente necesitas todos los campos definidos en UsuarioInfo
            cursor = db.rawQuery("SELECT * FROM usuarios", null)

            if (cursor.moveToFirst()) {
                // Obtener los índices de las columnas una vez fuera del bucle para eficiencia
                val idColumnIndex = cursor.getColumnIndex("id")
                val nombreColumnIndex = cursor.getColumnIndex("nombre")
                val correoColumnIndex = cursor.getColumnIndex("correo")
                val claveColumnIndex = cursor.getColumnIndex("clave")
                val tipoColumnIndex = cursor.getColumnIndex("tipo") // Asumiendo que la columna se llama 'tipo'

                // Validar que todas las columnas necesarias existen
                if (idColumnIndex == -1 || nombreColumnIndex == -1 || correoColumnIndex == -1 || claveColumnIndex == -1 || tipoColumnIndex == -1) {
                    Log.e("DatabaseError", "Una o más columnas requeridas no se encontraron en la tabla 'usuarios'.")
                    return usuarios // Retorna la lista vacía
                }

                do {
                    val id = cursor.getInt(idColumnIndex)
                    val nombre = cursor.getString(nombreColumnIndex)
                    val correo = cursor.getString(correoColumnIndex)
                    val clave = cursor.getString(claveColumnIndex)
                    val tipo = cursor.getString(tipoColumnIndex)

                    usuarios.add(DataClassesUsuario(id, nombre, correo, clave, tipo))
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("DatabaseError", "Error al obtener todos los usuarios: ${e.message}", e)
            // En caso de error, se devolverá la lista 'usuarios' que podría estar vacía o parcialmente llena
            // dependiendo de dónde ocurrió la excepción.
        } finally {
            cursor?.close() // Cerrar el cursor en el bloque finally para asegurar que se cierre
            // db.close() // Generalmente, SQLiteOpenHelper maneja el cierre de la base de datos.
            // No cierres 'db' aquí si es una propiedad gestionada por SQLiteOpenHelper.
        }
        return usuarios
    }

    // Obtener la lista de trabajadores
    fun obtenerTrabajadores(): List<DataClassesTrabajador> {
        // Asegúrate de que 'readableDatabase' es tu instancia de SQLiteDatabase.
        // Esto es un placeholder, debes obtener tu instancia de SQLiteDatabase.
        // Ejemplo: val db = miSQLiteOpenHelper.readableDatabase
        val db = readableDatabase // Reemplaza esto con tu acceso real a la BD

        val listaTrabajadores = mutableListOf<DataClassesTrabajador>()
        var cursor: Cursor? = null

        // Consulta SQL con JOIN para combinar información de ambas tablas
        // Se seleccionan todas las columnas necesarias de 'usuarios' (alias 'u') y 'trabajadores' (alias 't')
        // Se filtran los usuarios que también son trabajadores (existen en ambas tablas)
        val query = """
        SELECT u.id, u.nombre, u.correo, u.clave, u.tipo, 
               t.fechaDeNac, t.direccion, t.oficio, t.experiencia
        FROM usuarios u
        INNER JOIN trabajadores t ON u.id = t.id
    """.trimIndent()

        try {
            cursor = db.rawQuery(query, null)

            if (cursor.moveToFirst()) {
                // Obtener los índices de las columnas una vez para eficiencia
                val idCol = cursor.getColumnIndexOrThrow("id")
                val nombreCol = cursor.getColumnIndexOrThrow("nombre")
                val correoCol = cursor.getColumnIndexOrThrow("correo")
                val claveCol = cursor.getColumnIndexOrThrow("clave")
                val tipoCol = cursor.getColumnIndexOrThrow("tipo")
                val fechaDeNacCol = cursor.getColumnIndexOrThrow("fechaDeNac")
                val direccionCol = cursor.getColumnIndexOrThrow("direccion")
                val oficioCol = cursor.getColumnIndexOrThrow("oficio")
                val experienciaCol = cursor.getColumnIndexOrThrow("experiencia")

                do {
                    val id = cursor.getInt(idCol)
                    val nombre = cursor.getString(nombreCol)
                    val correo = cursor.getString(correoCol)
                    val clave = cursor.getString(claveCol)
                    val tipo = cursor.getString(tipoCol)
                    // Para los campos de la tabla 'trabajadores', es buena práctica
                    // manejar la posibilidad de que sean NULL si el esquema lo permite,
                    // aunque en tu CREATE TABLE no se especifica NOT NULL para ellos.
                    // Aquí se asume que siempre tendrán valor basado en tu DataClass.
                    val fechaDeNac = cursor.getString(fechaDeNacCol) ?: "" // Proporciona valor por defecto si es nulo
                    val direccion = cursor.getString(direccionCol) ?: ""
                    val oficio = cursor.getString(oficioCol) ?: ""
                    val experiencia = cursor.getString(experienciaCol) ?: ""

                    listaTrabajadores.add(
                        DataClassesTrabajador(
                            id, nombre, correo, clave, tipo,
                            fechaDeNac, direccion, oficio, experiencia
                        )
                    )
                } while (cursor.moveToNext())
            }
        } catch (e: IllegalArgumentException) {
            // Esto se captura si getColumnIndexOrThrow no encuentra una columna
            Log.e("DatabaseError", "Error al obtener índices de columna: ${e.message}", e)
        } catch (e: Exception) {
            Log.e("DatabaseError", "Error al obtener trabajadores: ${e.message}", e)
            // En caso de error, se devolverá la lista que podría estar vacía o parcialmente llena.
        } finally {
            cursor?.close()
            // No cierres 'db' aquí si es una propiedad gestionada por SQLiteOpenHelper,
            // ya que el helper se encarga de su ciclo de vida.
        }
        return listaTrabajadores
    }

    fun obtenerTrabajadorPorId(idTrabajador: Int): List<DataClassesTrabajador> {
        // Asegúrate de que 'readableDatabase' es tu instancia de SQLiteDatabase.
        // Esto es un placeholder, debes obtener tu instancia de SQLiteDatabase.
        // Ejemplo: val db = miSQLiteOpenHelper.readableDatabase
        val db = readableDatabase // Reemplaza esto con tu acceso real a la BD

        val listaTrabajadores = mutableListOf<DataClassesTrabajador>()
        var cursor: Cursor? = null

        // Consulta SQL con JOIN para combinar información de ambas tablas
        // Se seleccionan todas las columnas necesarias de 'usuarios' (alias 'u') y 'trabajadores' (alias 't')
        // Se filtran los usuarios que también son trabajadores (existen en ambas tablas)
        val query = """
        SELECT u.id, u.nombre, u.correo, u.clave, u.tipo, 
               t.fechaDeNac, t.direccion, t.oficio, t.experiencia
        FROM usuarios u 
        INNER JOIN trabajadores t ON u.id = t.id WHERE u.id = ?
    """.trimIndent()

        try {
            cursor = db.rawQuery(query, arrayOf(idTrabajador.toString()))

            if (cursor.moveToFirst()) {
                // Obtener los índices de las columnas una vez para eficiencia
                val idCol = cursor.getColumnIndexOrThrow("id")
                val nombreCol = cursor.getColumnIndexOrThrow("nombre")
                val correoCol = cursor.getColumnIndexOrThrow("correo")
                val claveCol = cursor.getColumnIndexOrThrow("clave")
                val tipoCol = cursor.getColumnIndexOrThrow("tipo")
                val fechaDeNacCol = cursor.getColumnIndexOrThrow("fechaDeNac")
                val direccionCol = cursor.getColumnIndexOrThrow("direccion")
                val oficioCol = cursor.getColumnIndexOrThrow("oficio")
                val experienciaCol = cursor.getColumnIndexOrThrow("experiencia")

                do {
                    val id = cursor.getInt(idCol)
                    val nombre = cursor.getString(nombreCol)
                    val correo = cursor.getString(correoCol)
                    val clave = cursor.getString(claveCol)
                    val tipo = cursor.getString(tipoCol)
                    // Para los campos de la tabla 'trabajadores', es buena práctica
                    // manejar la posibilidad de que sean NULL si el esquema lo permite,
                    // aunque en tu CREATE TABLE no se especifica NOT NULL para ellos.
                    // Aquí se asume que siempre tendrán valor basado en tu DataClass.
                    val fechaDeNac = cursor.getString(fechaDeNacCol) ?: "" // Proporciona valor por defecto si es nulo
                    val direccion = cursor.getString(direccionCol) ?: ""
                    val oficio = cursor.getString(oficioCol) ?: ""
                    val experiencia = cursor.getString(experienciaCol) ?: ""

                    listaTrabajadores.add(
                        DataClassesTrabajador(
                            id, nombre, correo, clave, tipo,
                            fechaDeNac, direccion, oficio, experiencia
                        )
                    )
                } while (cursor.moveToNext())
            }
        } catch (e: IllegalArgumentException) {
            // Esto se captura si getColumnIndexOrThrow no encuentra una columna
            Log.e("DatabaseError", "Error al obtener índices de columna: ${e.message}", e)
        } catch (e: Exception) {
            Log.e("DatabaseError", "Error al obtener trabajadores: ${e.message}", e)
            // En caso de error, se devolverá la lista que podría estar vacía o parcialmente llena.
        } finally {
            cursor?.close()
            // No cierres 'db' aquí si es una propiedad gestionada por SQLiteOpenHelper,
            // ya que el helper se encarga de su ciclo de vida.
        }
        return listaTrabajadores
    }

    // Insertar cliente
    fun insertarCliente(correo: String): Boolean {
        val db = writableDatabase
        val idUsuario = obtenerIdUsuarioPorCorreo(correo)

        if (idUsuario == null) return false

        val valores = ContentValues().apply {
            put("id", idUsuario) // Este es el mismo id que en la tabla usuarios
        }

        val resultado = db.insert("clientes", null, valores)
        return resultado != -1L
    }

    fun insertarTrabajador(idUsuario: Int, fechaNacimiento: String, direccion: String, oficio: String, experiencia: String): Boolean {
        val db = writableDatabase // Reemplaza esto con tu acceso real a la BD
        val valores = ContentValues().apply {
            put("id", idUsuario)
            put("fechaDeNac", fechaNacimiento)
            put("direccion", direccion)
            put("oficio", oficio)
            put("experiencia", experiencia)
        }

        var resultado: Long = -1L
        try {
            resultado = db.insert("trabajadores", null, valores)
            if (resultado == -1L) {
                Log.e("DB_INSERT_TRABAJADOR", "Falló la inserción del trabajador con ID de usuario: $idUsuario. Podría ser debido a una violación de FK (el usuario no existe) u otro error.")
            }
        } catch (e: Exception) {
            Log.e("DB_INSERT_TRABAJADOR", "Excepción al insertar trabajador con ID de usuario: $idUsuario. Error: ${e.message}", e)
            return false // Retorna false en caso de excepción
        }

        return resultado != -1L
    }

    // Validar login de usuario
    fun validarUsuario(correo: String, clave: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM usuarios WHERE correo = ? AND clave = ?", arrayOf(correo, clave))
        val existe = cursor.moveToFirst()
        cursor.close()
        return existe
    }

    // Verificar si el correo ya existe
    fun existeUsuario(correo: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id FROM usuarios WHERE correo = ?",
            arrayOf(correo)
        )
        val existe = cursor.count > 0
        cursor.close()
        return existe
    }

    // Obtener nombre de usuario por correo
    fun obtenerNombrePorCorreo(correo: String): String? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT nombre FROM usuarios WHERE correo = ?", arrayOf(correo))
        var nombre: String? = null
        if (cursor.moveToFirst()) {
            nombre = cursor.getString(0)
        }
        cursor.close()
        return nombre
    }

    // Insertar solicitud
    fun insertarSolicitud(estado: String, idCliente: Int, idTrabajador: Int): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("estado", estado)
            put("id_cliente", idCliente)
            put("id_trabajador", idTrabajador)
        }
        return db.insert("solicitud", null, valores) != -1L
    }

    // Cambiar estado de una solicitud
    fun actualizarEstadoSolicitud(idSolicitud: Int, nuevoEstado: String): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("estado", nuevoEstado)
        }
        val filas = db.update("solicitud", valores, "id = ?", arrayOf(idSolicitud.toString()))
        return filas > 0
    }


    fun obtenerSolicitudesPorTrabajador(idTrabajador: Int?): List<SolicitudInfo> {
        val db = readableDatabase
        val resultados = mutableListOf<SolicitudInfo>()
        var cursor: Cursor? = null

        val query = """
        SELECT
            solicitud.id AS idSolicitud,
            solicitud.estado,
            solicitud.id_cliente AS idCliente,
            solicitud.id_trabajador AS idTrabajador,
            usuarios_cliente.nombre AS nombreCliente,
            usuarios_trabajador.nombre AS nombreTrabajador
        FROM solicitud
        INNER JOIN clientes ON solicitud.id_cliente = clientes.id
        INNER JOIN usuarios AS usuarios_cliente ON clientes.id = usuarios_cliente.id
        INNER JOIN trabajadores ON solicitud.id_trabajador = trabajadores.id
        INNER JOIN usuarios AS usuarios_trabajador ON trabajadores.id = usuarios_trabajador.id
        WHERE solicitud.id_trabajador = ? AND solicitud.estado != 'terminado' AND solicitud.estado != 'rechazado' AND solicitud.estado != 'aceptado'
        ORDER BY solicitud.id DESC
        """.trimIndent()

        try {
            cursor = db.rawQuery(query, arrayOf(idTrabajador.toString()))

            if (cursor.moveToFirst()) {
                val idSolicitudCol = cursor.getColumnIndexOrThrow("idSolicitud")
                val estadoCol = cursor.getColumnIndexOrThrow("estado")
                val idClienteCol = cursor.getColumnIndexOrThrow("idCliente")
                val idTrabajadorCol = cursor.getColumnIndexOrThrow("idTrabajador")
                val nombreClienteCol = cursor.getColumnIndexOrThrow("nombreCliente")
                val nombreTrabajadorCol = cursor.getColumnIndexOrThrow("nombreTrabajador")

                do {
                    val idSolicitud = cursor.getInt(idSolicitudCol)
                    val estado = cursor.getString(estadoCol)
                    val idCliente = cursor.getInt(idClienteCol)
                    // The idTrabajador is already known as it's passed into the function,
                    // but we extract it from the cursor for consistency with SolicitudInfo
                    val idTrabajadorFromCursor = cursor.getInt(idTrabajadorCol)
                    val nombreCliente = cursor.getString(nombreClienteCol)
                    val nombreTrabajador = cursor.getString(nombreTrabajadorCol)

                    resultados.add(
                        SolicitudInfo(
                            idSolicitud,
                            idTrabajadorFromCursor, // Use the ID from the cursor
                            idCliente,
                            nombreTrabajador,
                            nombreCliente,
                            estado
                        )
                    )
                } while (cursor.moveToNext())
            }
        } catch (e: IllegalArgumentException) {
            Log.e("SqliteError", "Error al obtener índices de columna en obtenerSolicitudesPorTrabajador: ${e.message}", e)
        } catch (e: Exception) {
            Log.e("SqliteError", "Error al obtener solicitudes por trabajador: ${e.message}", e)
        } finally {
            cursor?.close()
        }
        return resultados
    }

    fun obtenerTodasLasSolicitudes(): List<SolicitudInfo> {
        val db = readableDatabase
        val listaSolicitudes = mutableListOf<SolicitudInfo>()
        var cursor: Cursor? = null

        val query = """
        SELECT
            solicitud.id AS idSolicitud,
            solicitud.estado,
            solicitud.id_cliente AS idCliente,          -- Nuevo: seleccionando id_cliente
            solicitud.id_trabajador AS idTrabajador,    -- Nuevo: seleccionando id_trabajador
            usuarios_cliente.nombre AS nombreCliente,
            usuarios_trabajador.nombre AS nombreTrabajador
        FROM solicitud
        INNER JOIN clientes ON solicitud.id_cliente = clientes.id
        INNER JOIN usuarios AS usuarios_cliente ON clientes.id = usuarios_cliente.id
        INNER JOIN trabajadores ON solicitud.id_trabajador = trabajadores.id
        INNER JOIN usuarios AS usuarios_trabajador ON trabajadores.id = usuarios_trabajador.id
        ORDER BY solicitud.id DESC
        """.trimIndent()

        try {
            cursor = db.rawQuery(query, null)

            if (cursor.moveToFirst()) {
                // Obteniendo los índices de todas las columnas requeridas por SolicitudInfo
                val idSolicitudCol = cursor.getColumnIndexOrThrow("idSolicitud")
                val estadoCol = cursor.getColumnIndexOrThrow("estado")
                val idClienteCol = cursor.getColumnIndexOrThrow("idCliente")          // Índice para idCliente
                val idTrabajadorCol = cursor.getColumnIndexOrThrow("idTrabajador")    // Índice para idTrabajador
                val nombreClienteCol = cursor.getColumnIndexOrThrow("nombreCliente")
                val nombreTrabajadorCol = cursor.getColumnIndexOrThrow("nombreTrabajador")

                do {
                    val idSolicitud = cursor.getInt(idSolicitudCol)
                    val estado = cursor.getString(estadoCol)
                    val idCliente = cursor.getInt(idClienteCol)          // Extrayendo idCliente
                    val idTrabajador = cursor.getInt(idTrabajadorCol)    // Extrayendo idTrabajador
                    val nombreCliente = cursor.getString(nombreClienteCol)
                    val nombreTrabajador = cursor.getString(nombreTrabajadorCol)

                    listaSolicitudes.add(
                        SolicitudInfo(
                            idSolicitud,
                            idTrabajador,
                            idCliente,
                            nombreTrabajador,
                            nombreCliente,
                            estado
                        )
                    )
                } while (cursor.moveToNext())
            }
        } catch (e: IllegalArgumentException) {
            Log.e("SqliteError", "Error de columna en obtenerTodasLasSolicitudes: ${e.message}", e)
        } catch (e: Exception) {
            Log.e("SqliteError", "Error al obtener todas las solicitudes: ${e.message}", e)
        } finally {
            cursor?.close()
        }
        return listaSolicitudes
    }

    fun obtenerTipoUsuarioPorCorreo(correo: String): String? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT tipo FROM usuarios WHERE correo = ?", arrayOf(correo))
        var tipo: String? = null
        if (cursor.moveToFirst()) {
            tipo = cursor.getString(0)
        }
        cursor.close()
        return tipo
    }

}