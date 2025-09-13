// Archivo: app/src/main/java/com/example/desafio_dsm2/datos/Nota.kt
package com.example.desafio_dsm2.datos

import java.io.Serializable

data class Nota(
    var estudianteKey: String? = null,
    var grado: String? = null,
    var materia: String? = null,
    var notaFinal: Double? = null,
    var key: String? = null
) : Serializable