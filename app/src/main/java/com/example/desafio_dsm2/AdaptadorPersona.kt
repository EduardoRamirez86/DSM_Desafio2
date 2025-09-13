package com.example.desafio_dsm2

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.desafio_dsm2.datos.Persona
import com.example.desafio_dsm2.R

class AdaptadorPersona(private val context: Activity, var personas: List<Persona>) :
    ArrayAdapter<Persona>(context, R.layout.persona_layout, personas) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = context.layoutInflater
        val rowView: View = convertView ?: layoutInflater.inflate(R.layout.persona_layout, parent, false)

        val tvNombre = rowView.findViewById<TextView>(R.id.tvNombre)
        val tvDUI = rowView.findViewById<TextView>(R.id.tvDUI)
        val tvApellido = rowView.findViewById<TextView>(R.id.tvApellido)
        val tvTelefono = rowView.findViewById<TextView>(R.id.tvTelefono)
        val tvEdad = rowView.findViewById<TextView>(R.id.tvEdad)
        val tvDireccion = rowView.findViewById<TextView>(R.id.tvDireccion)

        val persona = personas[position]

        tvNombre.text = "Nombre: ${persona.nombre}"
        tvDUI.text = "DUI: ${persona.dui}"
        tvApellido.text = "Apellido: ${persona.apellido}"
        tvTelefono.text = "Teléfono: ${persona.telefono}"
        tvEdad.text = "Edad: ${persona.edad}"
        tvDireccion.text = "Dirección: ${persona.direccion}"

        return rowView
    }
}