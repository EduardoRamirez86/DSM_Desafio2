// Archivo: app/src/main/java/com/example/desafio_dsm2/AdaptadorEstudiante.kt
package com.example.desafio_dsm2

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.desafio_dsm2.datos.Estudiante

class AdaptadorEstudiante(private val context: Activity, var estudiantes: List<Estudiante>) :
    ArrayAdapter<Estudiante>(context, R.layout.estudiante_layout, estudiantes) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = context.layoutInflater
        val rowView: View = convertView ?: layoutInflater.inflate(R.layout.estudiante_layout, parent, false)

        val tvNombreCompleto = rowView.findViewById<TextView>(R.id.tvNombreCompleto)
        val tvEdad = rowView.findViewById<TextView>(R.id.tvEdad)
        val tvDireccion = rowView.findViewById<TextView>(R.id.tvDireccion)
        val tvTelefono = rowView.findViewById<TextView>(R.id.tvTelefono)

        val estudiante = estudiantes[position]

        tvNombreCompleto.text = "Nombre: ${estudiante.nombreCompleto}"
        tvEdad.text = "Edad: ${estudiante.edad}"
        tvDireccion.text = "Dirección: ${estudiante.direccion}"
        tvTelefono.text = "Teléfono: ${estudiante.telefono}"

        return rowView
    }
}