// Archivo: app/src/main/java/com/example/desafio_dsm2/datos/Estudiante.kt
package com.example.desafio_dsm2.datos

import java.io.Serializable

data class Estudiante(
    var nombreCompleto: String? = null,
    var edad: Int? = null,
    var direccion: String? = null,
    var telefono: String? = null,
    var key: String? = null // Usado como clave generada por Firebase
) : Serializable