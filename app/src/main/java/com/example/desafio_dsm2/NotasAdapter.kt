package com.example.desafio_dsm2

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.desafio_dsm2.datos.EstudianteNota

class NotasAdapter(private val context: Activity, var notas: List<EstudianteNota>) :
    ArrayAdapter<EstudianteNota>(context, R.layout.nota_layout, notas) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = context.layoutInflater
        val rowView: View = convertView ?: layoutInflater.inflate(R.layout.nota_layout, parent, false)

        val tvNombreEstudiante = rowView.findViewById<TextView>(R.id.tvNombreEstudiante)
        val tvNotaFinal = rowView.findViewById<TextView>(R.id.tvNotaFinal)

        val notaEstudiante = notas[position]

        tvNombreEstudiante.text = notaEstudiante.nombre
        tvNotaFinal.text = "Nota: ${notaEstudiante.notaFinal}"

        return rowView
    }
}