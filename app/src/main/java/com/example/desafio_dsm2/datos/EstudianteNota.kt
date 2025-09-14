// Archivo: app/src/main/java/com/example/desafio_dsm2/datos/EstudianteNota.kt
package com.example.desafio_dsm2.datos

data class EstudianteNota(
    var nombre: String? = null,
    var notaFinal: Double? = null,
    var key: String? = null, // Clave de la nota
    var estudianteKey: String? = null // Clave del estudiante
)