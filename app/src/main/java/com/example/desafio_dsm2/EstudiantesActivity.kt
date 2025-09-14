package com.example.desafio_dsm2

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.desafio_dsm2.datos.Estudiante
import com.example.desafio_dsm2.datos.EstudianteAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class EstudiantesActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var refEstudiantes: DatabaseReference
    private lateinit var refNotas: DatabaseReference // Added for notes
    private lateinit var listaEstudiantes: ListView
    private lateinit var fabAgregarEstudiante: FloatingActionButton
    private lateinit var fabAgregarNota: FloatingActionButton

    private var estudiantes: MutableList<Estudiante> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estudiantes)

        auth = Firebase.auth
        refEstudiantes = FirebaseDatabase.getInstance().getReference("estudiantes")
        refNotas = FirebaseDatabase.getInstance().getReference("notas") // Initialized refNotas

        // 1. Inicializar las vistas
        try {
            listaEstudiantes = findViewById(R.id.ListaEstudiantes)
            fabAgregarEstudiante = findViewById(R.id.fab_agregar_estudiante)
            fabAgregarNota = findViewById(R.id.fab_agregar_nota)
        } catch (e: Exception) {
            Toast.makeText(this, "Error: Vistas no encontradas. Verifique su XML.", Toast.LENGTH_LONG).show()
            e.printStackTrace()
            return
        }

        // 2. Configurar los listeners
        configurarListeners()

        // 3. Cargar la lista de estudiantes al iniciar
        cargarEstudiantes()
    }

    private fun cargarEstudiantes() {
        refEstudiantes.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                estudiantes.clear()
                for (dato in dataSnapshot.children) {
                    try {
                        val estudiante: Estudiante? = dato.getValue(Estudiante::class.java)
                        estudiante?.key = dato.key
                        if (estudiante != null) {
                            estudiantes.add(estudiante)
                        }
                    } catch (e: com.google.firebase.database.DatabaseException) {
                        e.printStackTrace()
                    }
                }
                val adapter = EstudianteAdapter(this@EstudiantesActivity, estudiantes)
                listaEstudiantes.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@EstudiantesActivity, "Error al cargar datos.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun configurarListeners() {
        fabAgregarEstudiante.setOnClickListener {
            val intent = Intent(this, AddEstudianteActivity::class.java)
            startActivity(intent)
        }

        fabAgregarNota.setOnClickListener {
            val intent = Intent(this, NotasActivity::class.java)
            startActivity(intent)
        }

        listaEstudiantes.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, i, _ ->
            val estudiante = estudiantes[i]
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Eliminar Estudiante")
            builder.setMessage("¿Estás seguro de que quieres eliminar a ${estudiante.nombreCompleto} y todas sus notas asociadas?")
            builder.setPositiveButton("Sí") { dialog, which ->
                eliminarEstudiante(estudiante.key)
            }
            builder.setNegativeButton("No", null)
            builder.show()
            true
        }
    }

    // New method to handle the deletion
    private fun eliminarEstudiante(estudianteKey: String?) {
        if (estudianteKey == null) {
            Toast.makeText(this, "Error: La clave del estudiante es nula.", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(this@EstudiantesActivity, "Estudiante y notas eliminadas con éxito.", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@EstudiantesActivity, "Error al eliminar el estudiante.", Toast.LENGTH_SHORT).show()
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@EstudiantesActivity, "Error al eliminar notas asociadas.", Toast.LENGTH_SHORT).show()
                }
            })
    }
}