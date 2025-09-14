// Archivo: app/src/main/java/com/example/desafio_dsm2/datos/Nota.kt
package com.example.desafio_dsm2.datos

import com.google.firebase.database.Exclude

data class Nota(
    var estudianteKey: String? = null,
    var grado: String? = null,
    var materia: String? = null,
    var notaFinal: Double? = null,
    @get:Exclude
    var key: String? = null
)