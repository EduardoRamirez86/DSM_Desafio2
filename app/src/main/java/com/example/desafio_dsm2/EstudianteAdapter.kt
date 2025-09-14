package com.example.desafio_dsm2.datos

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.example.desafio_dsm2.EditEstudianteActivity
import com.example.desafio_dsm2.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EstudianteAdapter(private val mContext: Context, private val listaEstudiantes: List<Estudiante>) : ArrayAdapter<Estudiante>(mContext, 0, listaEstudiantes) {

    private lateinit var refEstudiantes: DatabaseReference
    private lateinit var refNotas: DatabaseReference

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItem = convertView
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.estudiante_layout, parent, false)
        }

        // Obtener la referencia a la base de datos
        refEstudiantes = FirebaseDatabase.getInstance().getReference("estudiantes")
        refNotas = FirebaseDatabase.getInstance().getReference("notas")

        val estudianteActual = listaEstudiantes[position]

        val tvNombre = listItem?.findViewById<TextView>(R.id.tvNombreCompleto)
        val tvEdad = listItem?.findViewById<TextView>(R.id.tvEdad)
        val tvDireccion = listItem?.findViewById<TextView>(R.id.tvDireccion)
        val tvTelefono = listItem?.findViewById<TextView>(R.id.tvTelefono)
        val btnEliminar = listItem?.findViewById<ImageButton>(R.id.btnEliminar)

        tvNombre?.text = "Nombre Completo: ${estudianteActual.nombreCompleto}"
        tvEdad?.text = "Edad: ${estudianteActual.edad}"
        tvDireccion?.text = "Dirección: ${estudianteActual.direccion}"
        tvTelefono?.text = "Teléfono: ${estudianteActual.telefono}"


        btnEliminar?.setOnClickListener {
            val builder = AlertDialog.Builder(mContext)
            builder.setTitle("Eliminar Estudiante")
            builder.setMessage("¿Estás seguro de que quieres eliminar a ${estudianteActual.nombreCompleto} y todas sus notas asociadas?")
            builder.setPositiveButton("Sí") { _, _ ->
                eliminarEstudiante(estudianteActual.key)
            }
            builder.setNegativeButton("No", null)
            builder.show()
        }


        listItem?.setOnClickListener {
            val intent = Intent(mContext, EditEstudianteActivity::class.java)
            intent.putExtra("id", estudianteActual.key)
            intent.putExtra("nombre", estudianteActual.nombreCompleto)
            intent.putExtra("edad", estudianteActual.edad)
            intent.putExtra("direccion", estudianteActual.direccion)
            intent.putExtra("telefono", estudianteActual.telefono)
            mContext.startActivity(intent)
        }

        return listItem!!
    }

    private fun eliminarEstudiante(estudianteKey: String?) {
        if (estudianteKey == null) {
            return
        }
        refNotas.orderByChild("estudianteKey").equalTo(estudianteKey)
            .addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                    if (snapshot.exists()) {
                        for (notaSnapshot in snapshot.children) {
                            notaSnapshot.ref.removeValue()
                        }
                    }
                    refEstudiantes.child(estudianteKey).removeValue()
                        .addOnSuccessListener {
                            // Puedes mostrar un Toast aquí si es necesario
                        }
                }
                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                    // Manejar error si es necesario
                }
            })
    }
}