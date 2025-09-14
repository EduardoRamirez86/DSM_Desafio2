// Archivo: app/src/main/java/com/example/desafio_dsm2/datos/EstudianteAdapter.kt
package com.example.desafio_dsm2.datos

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.desafio_dsm2.R
import com.google.firebase.database.*

class EstudianteAdapter(private val context: Context, private val estudiantes: MutableList<Estudiante>) :
    ArrayAdapter<Estudiante>(context, 0, estudiantes) {

    private val refEstudiantes = FirebaseDatabase.getInstance().getReference("estudiantes")
    private val refNotas = FirebaseDatabase.getInstance().getReference("notas")

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.estudiante_layout, parent, false)

        val estudiante = estudiantes[position]

        val tvNombreCompleto = view.findViewById<TextView>(R.id.tvNombreCompleto)
        val tvEdad = view.findViewById<TextView>(R.id.tvEdad)
        val tvDireccion = view.findViewById<TextView>(R.id.tvDireccion)
        val tvTelefono = view.findViewById<TextView>(R.id.tvTelefono)
        val btnEliminar = view.findViewById<ImageButton>(R.id.btnEliminar)

        tvNombreCompleto.text = "Nombre Completo: ${estudiante.nombreCompleto}"
        tvEdad.text = "Edad: ${estudiante.edad}"
        tvDireccion.text = "Dirección: ${estudiante.direccion}"
        tvTelefono.text = "Teléfono: ${estudiante.telefono}"

        // Configurar el listener para el botón de eliminar
        btnEliminar.setOnClickListener {
            // Llama a la función de eliminación
            eliminarEstudiante(estudiante.key)
        }

        return view
    }

    private fun eliminarEstudiante(estudianteKey: String?) {
        if (estudianteKey == null) {
            Toast.makeText(context, "Error: La clave del estudiante es nula.", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Eliminar todas las notas asociadas al estudiante
        refNotas.orderByChild("estudianteKey").equalTo(estudianteKey)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (notaSnapshot in snapshot.children) {
                            notaSnapshot.ref.removeValue()
                        }
                    }

                    // 2. Después de eliminar las notas, eliminar el estudiante
                    refEstudiantes.child(estudianteKey).removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(context, "Estudiante y notas eliminadas con éxito.", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error al eliminar el estudiante.", Toast.LENGTH_SHORT).show()
                        }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error al eliminar notas asociadas.", Toast.LENGTH_SHORT).show()
                }
            })
    }
}