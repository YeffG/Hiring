package com.example.hiringappv1

data class DataClassesUsuario(
    val id: Int,
    val nombre: String,
    val correo: String,
    val clave: String,
    val tipo: String
)

data class DataClassesTrabajador(
    val id: Int,
    val nombre: String,
    val correo: String,
    val clave: String,
    val tipo: String,
    val fechaDeNac: String,
    val direccion: String,
    val oficio: String,
    val experiencia: String
)

data class SolicitudInfo(
    val idSolicitud: Int,
    val idTrabajador: Int,
    val idCliente: Int,
    val nombreTrabajador: String,
    val nombreCliente: String,
    val estado: String
)

